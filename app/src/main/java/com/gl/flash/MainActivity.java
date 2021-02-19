package com.gl.flash;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements AccessibilityManager.AccessibilityStateChangeListener {

    private TextView pluginStatusText;
    private ImageView pluginStatusIcon;
    private AccessibilityManager accessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        pluginStatusText = findViewById(R.id.layout_control_accessibility_text);
        pluginStatusIcon = findViewById(R.id.layout_control_accessibility_icon);

        //监听AccessibilityService 变化
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        updateServiceStatus();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //移除监听服务
        accessibilityManager.removeAccessibilityStateChangeListener(this);
        super.onDestroy();
    }

    public void openAccessibility(View view) {
        try {
            Toast.makeText(this, getString(R.string.turn_on_toast) + pluginStatusText.getText(), Toast.LENGTH_SHORT).show();
            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(accessibleIntent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.turn_on_error_toast), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void openSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra("title", getString(R.string.preference));
        settingsIntent.putExtra("frag_id", "GeneralSettingsFragment");
        startActivity(settingsIntent);
    }


    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }

    /**
     * 更新当前 FlashService 显示状态
     */
    private void updateServiceStatus() {
        if (isServiceEnabled()) {
            pluginStatusText.setText(R.string.service_off);
            pluginStatusIcon.setBackgroundResource(R.drawable.ic_stop);
        } else {
            pluginStatusText.setText(R.string.service_on);
            pluginStatusIcon.setBackgroundResource(R.drawable.ic_start);
        }
    }

    /**
     * 获取 FlashService 是否启用状态
     */
    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.FlashService")) {
                return true;
            }
        }
        return false;
    }
}