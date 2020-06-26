package com.hobarb.fictional_spoon;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class IdeaPad extends AppCompatActivity {

    MyCanvas myCanvas;
    TextView clear;
    Button upload;
    EditText idea_text;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
StorageReference storageReference;
DatabaseReference databaseReference;

String image_url;
Uri uri;
String TIME_ID;
    String childinRD;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 77;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    Toast.makeText(IdeaPad.this, "Permission Granted", Toast.LENGTH_SHORT).show(); //a sample method called

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(IdeaPad.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_pad);
        myCanvas = findViewById(R.id.canvas_ip);
        idea_text = findViewById(R.id.editText_idea_text_ip);
        storageReference = FirebaseStorage.getInstance().getReference();
        TIME_ID = String.valueOf(System.currentTimeMillis());
        childinRD = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + TIME_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(childinRD);

        upload = findViewById(R.id.button_upload_ip);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(IdeaPad.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                {

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(IdeaPad.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
                    } else {
                        try {

                            takeScreenshot();
                              upload_file_also(uri);
                             // databaseReference.push().child("IMAGE_URL").setValue(image_url);

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage() + "", Toast.LENGTH_LONG).show();
                        }

                    }

                }

            }
        });
        clear = findViewById(R.id.textView_clear_ip);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear_canvas_ip(v);
            }
        });
    }

    private void takeScreenshot() {
        View u = findViewById(R.id.ll_1_ip);

        LinearLayout z = (LinearLayout) findViewById(R.id.ll_ip);
        int totalHeight = z.getHeight();
        int totalWidth = z.getWidth();

        Bitmap b = getBitmapFromView(u,totalHeight,totalWidth);

        //Save bitmap
        String extr = Environment.getExternalStorageDirectory().getPath() ;
        String fileName = "idea" + TIME_ID + ".jpg";
        File myPath = new File(extr, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), b, "Screen", "screen");
            Toast.makeText(getApplicationContext(), "Saved at " + extr + fileName, Toast.LENGTH_LONG).show();
            uri = Uri.fromFile(new File(extr+fileName));
            uri =  getImageContentUri(getApplicationContext(),  myPath);

        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Toast.makeText(getApplicationContext(), "" +e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {


        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void upload_file_also(Uri fileUri) {

        final StorageReference sr = storageReference.child(String.valueOf("Idea" + TIME_ID));
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ALL_IDEAS").document(childinRD);
        sr.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        image_url = uri.toString();
                        Toast.makeText(getApplicationContext(),"Uploaded to cloud!",  Toast.LENGTH_SHORT).show();
                        Map<String, Object > ideas= new HashMap<>();
                        ideas.put("IDEA_IMAGE", image_url);
                        ideas.put("IDEA_TEXT", idea_text.getText().toString());
                        documentReference.set(ideas).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Success!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

        });

    }

    public void clear_canvas_ip(View view) {
        myCanvas.path.reset();
        myCanvas.invalidate();

    }
}
