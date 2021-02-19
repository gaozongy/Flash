package com.gl.flash;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class FlashService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        if (rootNodeInfo == null) return;

        AccessibilityNodeInfo node1 = this.getTheLastNode(rootNodeInfo, "微信红包");
        if (node1 != null) {
            node1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
                        AccessibilityNodeInfo node2 = findOpenButton(rootNodeInfo);
                        if (node2 != null) {
                            node2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                },
                500);
    }

    @Override
    public void onInterrupt() {

    }

    private AccessibilityNodeInfo findOpenButton(AccessibilityNodeInfo node) {
        if (node == null)
            return null;

        //非layout元素
        if (node.getChildCount() == 0) {
            if ("android.widget.Button".equals(node.getClassName()))
                return node;
            else
                return null;
        }

        //layout元素，遍历找button
        AccessibilityNodeInfo button;
        for (int i = 0; i < node.getChildCount(); i++) {
            button = findOpenButton(node.getChild(i));
            if (button != null)
                return button;
        }
        return null;
    }

    private AccessibilityNodeInfo getTheLastNode(AccessibilityNodeInfo rootNodeInfo, String... texts) {
        int bottom = 0;
        AccessibilityNodeInfo lastNode = null, tempNode;
        List<AccessibilityNodeInfo> nodes;

        for (String text : texts) {
            if (text == null) continue;

            nodes = rootNodeInfo.findAccessibilityNodeInfosByText(text);

            if (nodes != null && !nodes.isEmpty()) {
                tempNode = nodes.get(nodes.size() - 1);
                if (tempNode == null) return null;
                Rect bounds = new Rect();
                tempNode.getBoundsInScreen(bounds);
                if (bounds.bottom > bottom) {
                    bottom = bounds.bottom;
                    lastNode = tempNode;
                }
            }
        }
        return lastNode;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
}