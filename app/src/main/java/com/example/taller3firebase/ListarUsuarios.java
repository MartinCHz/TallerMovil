package com.example.taller3firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


import com.example.taller3firebase.adapters.UsersAdapter;
import com.example.taller3firebase.model.DatabasePaths;
import com.example.taller3firebase.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListarUsuarios extends AppCompatActivity {
    private static final String TAG = ListarUsuarios.class.getName();
    private FirebaseAuth mAuth;

    // Auth user
    FirebaseUser currentUser;

    // Variables for Firebase DB
    FirebaseDatabase database;
    DatabaseReference myRef;
    ValueEventListener valueEventListener;

    //Local Data
    UsersAdapter adapter;
    ArrayList<User> userListLocal = new ArrayList<>();
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_usuarios);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Initialize Firebase database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // Initialize Adapter
        adapter = new UsersAdapter(ListarUsuarios.this, userListLocal);
        listView = findViewById(R.id.userslist);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            User user = userListLocal.get(position);
            Intent intent = new Intent(ListarUsuarios.this, MapsUsuario.class);
            intent.putExtra("user", user);
            Log.i(TAG, "User selected: " + user.toString());
            startActivity(intent);
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            logout();
        }
        loadQueryUser();
    }





    public void loadQueryUser() {
        myRef = database.getReference(DatabasePaths.USER);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                userListLocal.clear();
                for (DataSnapshot snapshot: datasnapshot.getChildren()) {
                    User ppl = snapshot.getValue(User.class);
                    ppl.setId(snapshot.getKey());
                    if(!ppl.getId().equals(currentUser.getUid()) && ppl.isAvailable()){
                        userListLocal.add(ppl);
                    }
                }
                adapter.notifyDataSetChanged();
                Log.i(TAG, "Data changed from realtime DB");
                listView.post(() -> listView.setSelection(userListLocal.size()-1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w(TAG, "Error en la consulta", databaseError.toException());
            }

        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (valueEventListener != null) {
            myRef.removeEventListener(valueEventListener);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemClicked = item.getItemId();
//        if(itemClicked == R.id.menuLogOut) {
//            logout();
//        }
//        return super.onOptionsItemSelected(item);
//    }


//    // Generate random people
//    public void generateUser(View view) {
//        Faker faker = new Faker();
//        User pl = new User();
//        pl.setFirstName(faker.funnyName().name());
//        pl.setLastName(faker.name().lastName());
//        pl.setAge(faker.date().birthday().getYear());
//        pl.setHeight(faker.number().randomDouble(1,1,2));
//        pl.setWeight(faker.number().randomDouble(1,50,150));
//        pl.setCurrentAddress(new Address());
//        pl.getCurrentAddress().setCity(faker.address().fullAddress());
//        pl.getCurrentAddress().setAddress(faker.address().fullAddress());
//
//        String key = myRef.push().getKey();
//        myRef = database.getReference(DatabasePaths.PEOPLE + key);
//        myRef.setValue(pl);
//    }
//
//
    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(ListarUsuarios.this, IniciarSesion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}