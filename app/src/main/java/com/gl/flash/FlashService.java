package com.gl.flash;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.util.List;


public class FlashService extends AccessibilityService {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
        if (rootNodeInfo == null) return;

        AccessibilityNodeInfo node1 = getTheLastNode(rootNodeInfo, "红包");
        if (node1 != null) {
            node1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }

//        GestureDescription.Builder builder = new GestureDescription.Builder();
//        Path path = new Path();
//        path.moveTo((float) 700, (float) 1600);
//        builder.addStroke(new GestureDescription.StrokeDescription(path, 1, 1));
//        final GestureDescription build = builder.build();
//        /**
//         * 参数GestureDescription：翻译过来就是手势的描述，如果要实现模拟，首先要描述你的腰模拟的手势嘛
//         * 参数GestureResultCallback：翻译过来就是手势的回调，手势模拟执行以后回调结果
//         * 参数handler：大部分情况我们不用的话传空就可以了
//         * 一般我们关注GestureDescription这个参数就够了，下边就重点介绍一下这个参数
//         */
//        dispatchGesture(build, new GestureResultCallback() {
//            public void onCancelled(GestureDescription gestureDescription) {
//                super.onCancelled(gestureDescription);
//            }
//            public void onCompleted(GestureDescription gestureDescription) {
//                super.onCompleted(gestureDescription);
//            }
//        }, null);

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
            if (TextUtils.equals(node.getClassName(), "android.widget.ImageView")) {
                node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
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