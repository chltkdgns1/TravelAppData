package com.gkftndltek.travelcourceapp.CustMap.Fragment_PersonalMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.gkftndltek.travelcourceapp.CustMap.Fragment_Home.HomeFragment;
import com.gkftndltek.travelcourceapp.CustMap.Fragment_PersonalMap.PersonRecycler.PersonClass;
import com.gkftndltek.travelcourceapp.CustMap.MainActivity;
import com.gkftndltek.travelcourceapp.CustMap.RecyclerView.DestinationDataClass;
import com.gkftndltek.travelcourceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class PersonalMapFragment extends Fragment implements MainActivity.OnBackPressedListeners {

    private String key = "l7xx713d4db3b29b418dba74f8af6162f4fb";
    private View rootView;
    private Context con;


    private ImageView ImageView_Menu2;
    // 데이터 베이스
    private String token, mapName;
    private FirebaseDatabase database;
    private DatabaseReference users, logined;

    // 프레그먼트
    HomeFragment homeFragment;


    // 리사이클

    PersonClass personClass;

    //  액티비티

    private Activity act;

    private Handler handlerPushMessage = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                DestinationDataClass data = (DestinationDataClass) msg.obj;
                personClass.add(data);
            }
            else if(msg.what == 2){
                DestinationDataClass data = (DestinationDataClass) msg.obj;
                NoneBitmapDestData noneBitmapDestData = new NoneBitmapDestData();
                noneBitmapDestData.setIndex(data.getIndex());
                noneBitmapDestData.setX(data.getX());
                noneBitmapDestData.setPhone(data.getPhone());
                noneBitmapDestData.setRoadAddress(data.getRoadAddress());
                noneBitmapDestData.setAddress(data.getAddress());
                noneBitmapDestData.setCategory(data.getCategory());
                noneBitmapDestData.setDescription(data.getDescription());
                noneBitmapDestData.setDrable(data.getDrable());
                noneBitmapDestData.setName(data.getName());
                noneBitmapDestData.setUrl(data.getUrl());
                noneBitmapDestData.setY(data.getY());

                System.out.println(data.getKey());
                users.child(token).child("maps").child(mapName).child("basket").child(data.getKey()).removeValue();
                users.child(token).child("maps").child(mapName).child("mylist").push().setValue(noneBitmapDestData);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.personal_map_activity, container, false);
        // 리니어레이아웃
        init();

        return rootView;
    }

    void init() {

        act = (MainActivity)getActivity();

        con = getActivity();

        homeFragment = new HomeFragment();
        personClass = new PersonClass();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        logined = database.getReference("logined");
        token = (((MainActivity)act)).token;
        mapName = (((MainActivity)act)).mapName;

        personClass.execute(rootView, con, handlerPushMessage);
        users.child(token).child("maps").child(mapName).child("basket").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapData : dataSnapshot.getChildren()) {
                    synchronized (this) {
                        new Thread() {
                            @Override
                            public void run() {
                                NoneBitmapDestData nondata = snapData.getValue(NoneBitmapDestData.class);
                                DestinationDataClass data = new DestinationDataClass();
                                data.setAddress(nondata.getAddress());
                                data.setCategory(nondata.getCategory());
                                data.setDescription(nondata.getDescription());
                                data.setDrable(nondata.getDrable());
                                data.setIndex(nondata.getIndex());
                                data.setLink(getImageBitmap(nondata.getUrl()));
                                data.setName(nondata.getName());
                                data.setPhone(nondata.getPhone());
                                data.setRoadAddress(nondata.getRoadAddress());
                                data.setUrl(nondata.getUrl());
                                data.setX(nondata.getX());
                                data.setY(nondata.getY());
                                data.setKey(snapData.getKey());

                                Message msg = Message.obtain();
                                msg.obj = data;
                                msg.what = 1;
                                handlerPushMessage.sendMessage(msg);
                            }
                        }.start();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        con = getActivity();

        ImageView_Menu2 = rootView.findViewById(R.id.ImageView_Menu2);

        ImageView_Menu2.setOnClickListener(new View.OnClickListener() { //  메뉴버튼 드러워아웃 나타남
            @Override
            public void onClick(View v) {
                ((MainActivity)act).drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {

        }
        return bm;
    }

    @Override
    public void onBack() {
        DrawerLayout drawer = ((MainActivity)act).drawerLayout;

        if (drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.closeDrawer(Gravity.LEFT);
        }
        else {
            if(getActivity()!=null)
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, homeFragment).commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setOnBackPressedListener(this); // 프레그먼트
    }
}
