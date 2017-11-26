package com.peac.cock.peacock_project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.peac.cock.peacock_project.projectAdapter.CategoryGridViewAdapter;
import com.peac.cock.peacock_project.projectDto.Category;

/**
 * Created by wee on 2017. 11. 15..
 */

public class CategoryAddActivity extends AppCompatActivity {

    private GridView categoryAddGridView;
    private ImageButton incomingButton;
    private ImageButton outgoingButton;
    private ImageButton transferButton;
    private ImageButton backButton;
    private Category mCategory;
    private Button categoryAddButton;
    private String categoryType;
    private String categoryImageString;
    private int categoryImageId;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String uid;
    private Intent intent;

    String[] gridViewString = {
            "아이", "뷰티", "카페",
            "차", "담배", "문화",
            "애완", "음료", "교육",
            "음식", "교양", "헬스",
            "집", "생활", "사랑",
            "휴대폰", "교통", "여행"
    };
    int[] gridViewImageId = {
            R.drawable.category_item_baby, R.drawable.category_item_beauty, R.drawable.category_item_caffe,
            R.drawable.category_item_car, R.drawable.category_item_cigarette, R.drawable.category_item_culture,
            R.drawable.category_item_dog, R.drawable.category_item_drink, R.drawable.category_item_education,
            R.drawable.category_item_food, R.drawable.category_item_gyeong, R.drawable.category_item_health,
            R.drawable.category_item_home, R.drawable.category_item_life, R.drawable.category_item_love,
            R.drawable.category_item_phone, R.drawable.category_item_traffic, R.drawable.category_item_trip
    };

    String[] matchGridViewString = {
            "육아", "의복/미용", "카페/간식",
            "자동차", "카페/간식", "문화/여가",
            "애완", "카페/간식", "교육",
            "식사", "문화/여가", "의료/건강",
            "주거/통신", "교통", "여행/숙박"
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_category_add);

        intent = getIntent();
        categoryType = intent.getStringExtra("categoryType");

        //fireBase Auth & Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        for (Integer i : gridViewImageId) {
            System.out.print(i);
        }

        //getId
        incomingButton = findViewById(R.id.category_add_layout_incoming_button);
        outgoingButton = findViewById(R.id.category_add_layout_outgoing_button);
        transferButton = findViewById(R.id.category_add_layout_transfer_button);
        categoryAddButton = findViewById(R.id.category_add_grid_view_category_add_button);
        backButton = findViewById(R.id.category_add_back_button);

        CategoryGridViewAdapter adapterViewCategory = new CategoryGridViewAdapter(getApplicationContext(), gridViewString, gridViewImageId);
        mCategory = new Category();
        //get Id
        categoryAddGridView = findViewById(R.id.category_add_grid_view_image_text);

        categoryAddGridView.setAdapter(adapterViewCategory);

        categoryAddGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Toast.makeText(getApplicationContext(), "String" + matchGridViewString[i] + "ID" + gridViewImageId[i], Toast.LENGTH_LONG).show();
                System.out.println(categoryType);
                mCategory.setCateImageString(matchGridViewString[i]);
                mCategory.setCateImageId(gridViewImageId[i]);
            }
        });

        // 초기 상태
        if (categoryType.equals("지출")) {
            outgoingButton.setBackgroundResource(R.drawable.handwriting_layout_active_outgoing_button);
            incomingButton.setBackgroundResource(R.drawable.handwriting_layout_incoming_button);
            transferButton.setBackgroundResource(R.drawable.handwriting_layout_transfer_button);
        } else if (categoryType.equals("수입")) {
            incomingButton.setBackgroundResource(R.drawable.handwriting_layout_active_incoming_button);
            outgoingButton.setBackgroundResource(R.drawable.handwriting_layout_outgoing_button);
            transferButton.setBackgroundResource(R.drawable.handwriting_layout_transfer_button);
        } else {
            transferButton.setBackgroundResource(R.drawable.handwriting_layout_active_transfer_button);
            incomingButton.setBackgroundResource(R.drawable.handwriting_layout_incoming_button);
            outgoingButton.setBackgroundResource(R.drawable.handwriting_layout_outgoing_button);
        }


        outgoingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                categoryType = "지출";
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    outgoingButton.setBackgroundResource(R.drawable.handwriting_layout_active_outgoing_button);
                    incomingButton.setBackgroundResource(R.drawable.handwriting_layout_incoming_button);
                    transferButton.setBackgroundResource(R.drawable.handwriting_layout_transfer_button);
                }
                return false;
            }
        });

        incomingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                categoryType = "수입";
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    incomingButton.setBackgroundResource(R.drawable.handwriting_layout_active_incoming_button);
                    outgoingButton.setBackgroundResource(R.drawable.handwriting_layout_outgoing_button);
                    transferButton.setBackgroundResource(R.drawable.handwriting_layout_transfer_button);
                }
                return false;
            }
        });

        transferButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                categoryType = "이체";
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    transferButton.setBackgroundResource(R.drawable.handwriting_layout_active_transfer_button);
                    incomingButton.setBackgroundResource(R.drawable.handwriting_layout_incoming_button);
                    outgoingButton.setBackgroundResource(R.drawable.handwriting_layout_outgoing_button);
                }
                return false;
            }
        });

        categoryAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.getReference().child("users").child(uid).child("category").child(categoryType).push().setValue(mCategory);
                intent.setClass(getApplicationContext(), CategoryActivity.class);
                intent.putExtra("categoryType", categoryType);
                startActivity(intent);
     //           finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(), CategoryActivity.class);
                intent.putExtra("categoryType", categoryType);
                startActivity(intent);
            }
        });

    }
}
