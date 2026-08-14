package com.pulsepay.contactless;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout activityList;
    private TextView balanceText;
    private double balance = 4286.40;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(244, 247, 246));
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ScrollView scroll = new ScrollView(this);
        LinearLayout page = column();
        page.setPadding(dp(20), dp(18), dp(20), dp(28));
        scroll.addView(page);

        LinearLayout header = row();
        LinearLayout titles = column();
        titles.addView(text("PulsePay", 24, Color.rgb(20, 36, 33), true));
        titles.addView(text("下午好，何先生", 13, Color.rgb(102, 116, 112), false));
        header.addView(titles, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView bell = text("●", 17, Color.rgb(8, 127, 116), true);
        bell.setGravity(Gravity.CENTER); bell.setBackground(shape(Color.WHITE, 22));
        header.addView(bell, new LinearLayout.LayoutParams(dp(44), dp(44)));
        page.addView(header);

        LinearLayout wallet = column();
        wallet.setPadding(dp(20), dp(18), dp(20), dp(18));
        wallet.setBackground(gradient());
        LinearLayout.LayoutParams walletParams = match(); walletParams.setMargins(0, dp(22), 0, 0);
        page.addView(wallet, walletParams);
        wallet.addView(text("可用余额", 13, Color.rgb(211, 242, 237), false));
        balanceText = text(money(balance), 32, Color.WHITE, true);
        wallet.addView(balanceText);
        TextView card = text("VISA  ••••  0824", 13, Color.WHITE, true);
        LinearLayout.LayoutParams cardParams = match(); cardParams.setMargins(0, dp(24), 0, 0);
        wallet.addView(card, cardParams);

        LinearLayout actions = row();
        actions.setPadding(0, dp(18), 0, dp(18));
        actions.addView(action("⌁", "轻触付款", v -> startPayment()), weighted());
        actions.addView(action("↗", "转账", v -> toast("转账功能已准备")), weighted());
        actions.addView(action("▦", "收款码", v -> showInfo("我的收款码", "演示收款码编号：PP-20824")), weighted());
        actions.addView(action("…", "更多", v -> toast("更多服务即将上线")), weighted());
        page.addView(actions);

        LinearLayout safety = row();
        safety.setGravity(Gravity.CENTER_VERTICAL); safety.setPadding(dp(16), dp(14), dp(16), dp(14));
        safety.setBackground(shape(Color.rgb(230, 243, 239), 8));
        safety.addView(text("✓", 18, Color.rgb(8,127,116), true));
        TextView safetyCopy = text("  NFC 安全支付已开启", 13, Color.rgb(31,83,73), true);
        safety.addView(safetyCopy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        safety.addView(text("设备就绪", 11, Color.rgb(8,127,116), false));
        page.addView(safety);

        page.addView(sectionTitle("最近活动", "查看全部", v -> toast("已显示最近交易")));
        activityList = column();
        page.addView(activityList);
        addTransaction("青禾咖啡", "今天 14:26 · 非接触支付", -36.00, false);
        addTransaction("城市交通", "今天 09:12 · 手机钱包", -4.00, false);
        addTransaction("好友转入", "昨天 20:45 · 即时到账", 268.00, false);

        LinearLayout store = row();
        store.setGravity(Gravity.CENTER_VERTICAL); store.setPadding(dp(16), dp(14), dp(16), dp(14));
        store.setBackground(shape(Color.WHITE, 8));
        LinearLayout storeCopy = column();
        storeCopy.addView(text("随时管理每笔消费", 14, Color.rgb(20,36,33), true));
        storeCopy.addView(text("官方 Android 客户端", 11, Color.rgb(102,116,112), false));
        store.addView(storeCopy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        ImageView google = new ImageView(this);
        google.setImageResource(R.drawable.google); google.setScaleType(ImageView.ScaleType.FIT_CENTER);
        google.setContentDescription("Get it on Google Play");
        store.addView(google, new LinearLayout.LayoutParams(dp(140), dp(49)));
        LinearLayout.LayoutParams storeParams = match(); storeParams.setMargins(0, dp(22), 0, 0);
        page.addView(store, storeParams);
        setContentView(scroll);
    }

    private void startPayment() {
        EditText input = new EditText(this); input.setHint("0.00"); input.setTextSize(26);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        new AlertDialog.Builder(this).setTitle("轻触付款").setMessage("输入演示付款金额").setView(input)
                .setNegativeButton("取消", null).setPositiveButton("继续", (d, w) -> {
                    try {
                        double amount = Double.parseDouble(input.getText().toString());
                        if (amount <= 0 || amount > balance) throw new NumberFormatException();
                        confirmPayment(amount);
                    } catch (NumberFormatException e) { toast("请输入有效且不超过余额的金额"); }
                }).show();
    }

    private void confirmPayment(double amount) {
        new AlertDialog.Builder(this).setTitle("靠近读卡设备")
                .setMessage(money(amount) + "\n\n请将手机靠近支持非接触支付的终端。此流程仅为本地演示。")
                .setNegativeButton("取消", null).setPositiveButton("模拟完成", (d,w) -> {
                    balance -= amount; balanceText.setText(money(balance));
                    String now = new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date());
                    addTransaction("非接触付款", "今天 " + now + " · PulsePay", -amount, true);
                    showInfo("付款成功", money(amount) + " 已从余额扣除");
                }).show();
    }

    private void addTransaction(String title, String subtitle, double amount, boolean first) {
        LinearLayout item = row(); item.setGravity(Gravity.CENTER_VERTICAL); item.setPadding(dp(14), dp(14), dp(14), dp(14));
        item.setBackground(shape(Color.WHITE, 0));
        TextView icon = text(amount > 0 ? "↓" : "↗", 16, amount > 0 ? Color.rgb(8,127,116) : Color.rgb(37,52,48), true);
        icon.setGravity(Gravity.CENTER); icon.setBackground(shape(Color.rgb(232,240,237), 22));
        item.addView(icon, new LinearLayout.LayoutParams(dp(42), dp(42)));
        LinearLayout copy = column(); copy.setPadding(dp(12), 0, 0, 0);
        copy.addView(text(title, 14, Color.rgb(20,36,33), true)); copy.addView(text(subtitle, 11, Color.rgb(105,117,113), false));
        item.addView(copy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        item.addView(text((amount > 0 ? "+" : "") + money(amount), 14, amount > 0 ? Color.rgb(8,127,116) : Color.rgb(20,36,33), true));
        item.setOnClickListener(v -> showInfo(title, subtitle + "\n状态：已完成\n金额：" + money(amount)));
        if (first) activityList.addView(item, 0); else activityList.addView(item);
    }

    private View sectionTitle(String left, String right, View.OnClickListener click) {
        LinearLayout bar = row(); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(0, dp(24), 0, dp(10));
        bar.addView(text(left, 17, Color.rgb(20,36,33), true), new LinearLayout.LayoutParams(0, dp(36), 1));
        TextView link = text(right, 12, Color.rgb(8,127,116), true); link.setGravity(Gravity.CENTER); link.setOnClickListener(click);
        bar.addView(link, new LinearLayout.LayoutParams(dp(72), dp(36))); return bar;
    }
    private View action(String icon, String title, View.OnClickListener click) {
        LinearLayout box = column(); box.setGravity(Gravity.CENTER); box.setOnClickListener(click); box.setContentDescription(title);
        TextView mark = text(icon, 22, Color.rgb(8,127,116), true); mark.setGravity(Gravity.CENTER); mark.setBackground(shape(Color.WHITE, 25));
        box.addView(mark, new LinearLayout.LayoutParams(dp(50), dp(50))); box.addView(text(title, 11, Color.rgb(43,57,53), true)); return box;
    }
    private LinearLayout row() { LinearLayout v = new LinearLayout(this); v.setOrientation(LinearLayout.HORIZONTAL); return v; }
    private LinearLayout column() { LinearLayout v = new LinearLayout(this); v.setOrientation(LinearLayout.VERTICAL); return v; }
    private TextView text(String value, int size, int color, boolean bold) { TextView v = new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(color); if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return v; }
    private LinearLayout.LayoutParams match() { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); }
    private LinearLayout.LayoutParams weighted() { return new LinearLayout.LayoutParams(0, dp(82), 1); }
    private GradientDrawable shape(int color, float radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private GradientDrawable gradient() { GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{Color.rgb(8,127,116), Color.rgb(22,83,91)}); d.setCornerRadius(dp(10)); return d; }
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private void toast(String value) { Toast.makeText(this, value, Toast.LENGTH_SHORT).show(); }
    private void showInfo(String title, String message) { new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("关闭", null).show(); }
    private static String money(double amount) { return NumberFormat.getCurrencyInstance(Locale.CHINA).format(amount); }
}
