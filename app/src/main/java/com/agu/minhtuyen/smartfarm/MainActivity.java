package com.agu.minhtuyen.smartfarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private AwesomeSpeedometer spmHumid, spmTemp;
    private DatabaseReference mDatabase;
    private ArrayList<CardItem> regions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_main);
        spmTemp = (AwesomeSpeedometer) findViewById(R.id.spMetter1);
        spmHumid = (AwesomeSpeedometer) findViewById(R.id.spMetter2);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter(MainActivity.this);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    1
            );
        } else {
            Toast.makeText(this,
                    "Xin chào " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            loadData();
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                mDatabase.child("regions").child(position + "").child("sensor").
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                float temp = dataSnapshot.child("temp").getValue(Float.class);
                                float humid = dataSnapshot.child("humid").getValue(Float.class);
                                String parent = dataSnapshot.getRef().getParent().getKey();
                                if (parent.contentEquals(String.valueOf(mViewPager.getCurrentItem()))) {
                                    spmTemp.setSpeedAt(temp);
                                    spmHumid.setSpeedAt(humid);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                mDatabase.child("regions").child(position + "").child("control").
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean auto = dataSnapshot.child("auto").getValue(Boolean.class);
                                boolean pump = dataSnapshot.child("pump").getValue(Boolean.class);
                                String parent = dataSnapshot.getRef().getParent().getKey();
                                if (parent.contentEquals(String.valueOf(mViewPager.getCurrentItem()))) {
                                    HashMap<String, Boolean> control = new HashMap<>();
                                    control.put("auto", auto);
                                    control.put("pump", pump);
                                    mCardAdapter.getmData().get(position).setControl(control);
                                    mCardAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Đăng nhập thành công!",
                        Toast.LENGTH_LONG)
                        .show();
                loadData();
            } else {
                Toast.makeText(this,
                        "Không thể đăng nhập. Vui lòng thử lại!",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "Đăng xuất thành công!.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }

    public void loadData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child("regions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<CardItem>> t = new GenericTypeIndicator<ArrayList<CardItem>>() {
                };
                regions = dataSnapshot.getValue(t);
                for (CardItem item : regions)
                    mCardAdapter.addCardItem(item);
                //mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
                //mCardShadowTransformer.enableScaling(true);
                mViewPager.setAdapter(mCardAdapter);
                //mViewPager.setPageTransformer(true, mCardShadowTransformer);
                mViewPager.setCurrentItem(1, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
