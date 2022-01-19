package com.gl.flash;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;


public class FlashService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.e("gaozy", "eventType:" + eventType);
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            String className = event.getClassName().toString();
            Log.e("gaozy", "className:" + className);

            if (TextUtils.equals(className, "com.tencent.wework.msg.controller.MessageListActivity")) {
                findRedPackets();
                return;
            }

            if (TextUtils.equals(className, "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeCollectorWithCoverActivity")) {
                openRedPackets();
                return;
            }

            if (TextUtils.equals(className, "com.tencent.wework.enterprise.redenvelopes.controller.RedEnvelopeDetailWithCoverActivity")) {
                backToChatList();
                return;
            }

            findRedPackets();
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
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
        findOpenButton(rootNodeInfo, nodeInfoList);
        if (nodeInfoList.size() >= 2) {
            nodeInfoList.get(nodeInfoList.size() - 2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
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


