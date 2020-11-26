package com.example.profitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore db;
    MyUser myUser;

    EditText mLastName,mFirstNAme ,mEmail, mPassword,mPhone,mDayOfBirth;
    RadioGroup mSex;
    Button mRegisterBtn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /* set variables */
        db = FirebaseFirestore.getInstance();
        System.out.println(db);
        mLastName = findViewById(R.id.lastName);
        mFirstNAme = findViewById(R.id.firstName);
        mPhone = findViewById(R.id.phoneNumber);
        mDayOfBirth = findViewById(R.id.dayofbirth);
        mSex = findViewById(R.id.sexgender);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        mRegisterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==mRegisterBtn) {
            //
            // test5();
            if (chackData()) {
                CreateUser();
                mAuth.createUserWithEmailAndPassword(myUser.getEmail(), myUser.getPassword())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("create user", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    updateUser(user);
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("fail create", "createUserWithEmail:failure", task.getException());
                                    //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    //      Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });

            }
            else {
                System.out.println("hello found out no!!!");
            }
        }

    }

    private void CreateUser() {
        myUser = new MyUser(mFirstNAme.getText().toString()
                ,mLastName.getText().toString(),mEmail.getText().toString()
                ,mPassword.getText().toString(),mPhone.getText().toString(),mDayOfBirth.getText().toString(),mSex.getCheckedRadioButtonId());
    }

    private boolean chackData() {
        boolean select = mSex.isSelected();
        boolean stringNotNullOrBlank = !mLastName.getText().toString().equals("")&&
                !mFirstNAme.getText().toString().equals("")&&
                !mEmail.getText().toString().equals("")&&
                !mPhone.getText().toString().equals("")&&
                !mPassword.getText().toString().equals("");
        return true;

    }


    void updateUser(FirebaseUser user){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(mLastName.getText().toString())
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();
        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("updateUser", "User profile updated.");
                        }
                    }
                });
        Map<String, Object> userDB = new HashMap<>();
        userDB.put("first", myUser.getFirstName());
        userDB.put("last", myUser.getLastName());
        userDB.put("born", myUser.getBirthDAy());
        userDB.put("sex", myUser.getGsex());
        userDB.put("phone", myUser.getPhone());

// Add a new document with a generated ID

                db.collection("users").document(user.getUid())
                        .set(userDB)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("add new user to db", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("fail add new user to db", "Error writing document", e);
                            }
                        });
    }
}