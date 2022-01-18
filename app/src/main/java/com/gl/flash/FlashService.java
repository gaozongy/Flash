package com.gl.flash;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class FlashService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        if (rootNodeInfo == null) return;

        AccessibilityNodeInfo node1 = getTheLastNode(rootNodeInfo, "红包");
        if (node1 != null) {
            node1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }

        AccessibilityNodeInfo node2 = getTheLastNode(rootNodeInfo, "看看大家的手气");
        if (node2 != null) {
            node2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }

        new android.os.Handler().postDelayed(() -> {
                    AccessibilityNodeInfo rootNodeInfo1 = getRootInActiveWindow();
                    AccessibilityNodeInfo node3 = findOpenButton(rootNodeInfo1);
                    if (node3 != null) {
                        node3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
            if (TextUtils.equals(node.getClassName(), "android.widget.Button")) {
                return node;
            } else {
                return null;
            }
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
        AccessibilityNodeInfo lastNode = null;
        AccessibilityNodeInfo tempNode;
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
}