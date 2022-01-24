package com.gl.flash;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


public class FlashService extends AccessibilityService {

    public static int delay = 0;

    private String mCurrentPage;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String value = sharedPreferences.getString("delay_time", "0");
        delay = Integer.parseInt(value);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String className = event.getClassName().toString();
        if (className.startsWith("com.tencent.wework")) {
            mCurrentPage = className;
        }

        Log.e("gaozy", "----------------------------------------------------------------");
        Log.e("gaozy", "eventType:" + eventType);
        Log.e("gaozy", "mCurrentPage:" + mCurrentPage);
        Log.e("gaozy", "className:" + className);

        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                && TextUtils.equals(mCurrentPage, "com.tencent.wework.msg.controller.MessageListActivity")) {
            findRedPackets();
            return;
        }

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // 已经进入红包二级页面，点击"开"按钮
            if (TextUtils.equals(mCurrentPage, "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeCollectorWithCoverActivity")) {
                openRedPackets();
                return;
            }

            // 红包已打开，返回聊天界面
            if (TextUtils.equals(mCurrentPage, "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeDetailWithCoverActivity")) {
                backToChatList();
            }
        }
    }

    private void findRedPackets() {
        Log.e("gaozy", "findRedPackets");
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo packetsNode = getTheLastNode(rootNodeInfo, "红包");
        if (packetsNode != null && packetsNode.getText() != null && !packetsNode.getText().toString().contains("领取了")) {
            packetsNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void openRedPackets() {
        Log.e("gaozy", "openRedPackets");
        new Handler().postDelayed(() -> {
                    AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
                    findOpenButton(rootNodeInfo, nodeInfoList);
                    if (nodeInfoList.size() >= 2) {
                        // 倒数第二个 ImageView 为打开按钮
                        nodeInfoList.get(nodeInfoList.size() - 2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                },
                delay);
    }

    private void backToChatList() {
        Log.e("gaozy", "backToChatList");
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {

    }

    private AccessibilityNodeInfo getTheLastNode(AccessibilityNodeInfo node, String text) {
        if (node == null) {
            return null;
        }

        int bottom = 0;
        AccessibilityNodeInfo resultNode = null;

        List<AccessibilityNodeInfo> nodes = node.findAccessibilityNodeInfosByText(text);
        for (AccessibilityNodeInfo temp : nodes) {
            if (temp == null) continue;
            Rect bounds = new Rect();
            temp.getBoundsInScreen(bounds);
            if (bounds.bottom > bottom) {
                bottom = bounds.bottom;
                resultNode = temp;
            }
        }
        return resultNode;
    }

    private void findOpenButton(AccessibilityNodeInfo node, List<AccessibilityNodeInfo> nodeInfoList) {
        if (node == null)
            return;

        if (node.getChildCount() == 0) {
            if (TextUtils.equals(node.getClassName(), "android.widget.ImageView")) {
                nodeInfoList.add(node);
            } else {
                return;
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findOpenButton(node.getChild(i), nodeInfoList);
        }
    }
}


