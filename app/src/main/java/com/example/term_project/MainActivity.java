package com.example.term_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView tvPlayerName;
    private TextView tvGold;

    // activity_main.xml에 추가한 overlay용 컨테이너
    private View fragmentContainer;

    // 게임 재화(골드)
    private int gold = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View 연결
        viewPager = findViewById(R.id.viewPager);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvGold = findViewById(R.id.tvGold);
        fragmentContainer = findViewById(R.id.fragment_container);

        // ViewPager 설정
        viewPager.setAdapter(new ViewPagerAdapter(this));
        viewPager.setCurrentItem(1, false);

        // 저장된 유저 닉네임 불러오기
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String nickname = prefs.getString("name", "기본닉네임");

        // 상단 정보 표시
        tvPlayerName.setText(nickname);
        updateTopBar();

        // 뒤로가기 처리
        // overlay fragment가 열려 있으면 그것부터 닫고,
        // 없으면 원래 앱 뒤로가기 동작 수행
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();

                    // popBackStack 이후 컨테이너를 숨길지 확인
                    getSupportFragmentManager().executePendingTransactions();

                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        fragmentContainer.setVisibility(View.GONE);
                    }
                } else {
                    finish();
                }
            }
        });
    }

    // 상단 골드 표시 갱신
    public void updateTopBar() {
        tvGold.setText(String.valueOf(gold));
    }

    // 골드 추가 ex)  ((MainActivity) getActivity()).addGold(50);
    public void addGold(int amount) {
        gold += amount;
        updateTopBar();
    }

    // 골드 사용  ex)  boolean success = ((MainActivity) getActivity()).spendGold(200);
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            updateTopBar();
            return true;
        }
        return false;
    }

    // 현재 골드 반환
    public int getGold() {
        return gold;
    }

    // ViewPager 위에 새로운 Fragment를 띄우는 함수
    // 예: QuizSelectFragment, QuizPlayFragment 등
    public void openFragment(Fragment fragment) {
        fragmentContainer.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // 현재 overlay fragment를 닫는 함수
    public void closeCurrentFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

            getSupportFragmentManager().executePendingTransactions();

            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                fragmentContainer.setVisibility(View.GONE);
            }
        } else {
            fragmentContainer.setVisibility(View.GONE);
        }
    }
}