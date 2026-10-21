package com.example.streetemu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * StreetEmu launcher shell. This activity manages a user-selected game folder and
 * local runtime preferences. A native game runtime must be integrated separately.
 */
public class MainActivity extends Activity {
    private static final int PICK_FOLDER = 42;
    private static final int BG = Color.rgb(10, 14, 22);
    private static final int PANEL = Color.rgb(22, 29, 42);
    private static final int PANEL_ALT = Color.rgb(29, 39, 55);
    private static final int ACCENT = Color.rgb(71, 226, 202);
    private static final int TEXT = Color.rgb(245, 248, 252);
    private static final int MUTED = Color.rgb(157, 171, 190);

    private SharedPreferences prefs;
    private LinearLayout root;
    private TextView folderTitle, folderDetail, validationStatus, runtimeStatus, settingsSummary;
    private Uri selectedFolder;
    private boolean folderValid;
    private List<String> missingItems = new ArrayList<>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("streetemu", MODE_PRIVATE);
        String saved = prefs.getString("gameFolder", "");
        if (!saved.isEmpty()) selectedFolder = Uri.parse(saved);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildHome();
        if (selectedFolder != null) validateSelectedFolder();
        updateSettingsSummary();
    }

    private GradientDrawable shape(int color, int radiusDp, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        if (strokeColor != 0) d.setStroke(dp(1), strokeColor);
        return d;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setIncludeFontPadding(true);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private void addGap(LinearLayout parent, int heightDp) {
        View gap = new View(this);
        parent.addView(gap, new LinearLayout.LayoutParams(1, dp(heightDp)));
    }

    private TextView button(String label, boolean primary, Runnable action) {
        TextView t = text(label, 14, primary ? BG : TEXT, true);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(14), dp(15), dp(14), dp(15));
        t.setBackground(shape(primary ? ACCENT : PANEL_ALT, 16, primary ? 0 : Color.rgb(55, 72, 91)));
        t.setClickable(true);
        t.setFocusable(true);
        t.setOnClickListener(v -> action.run());
        return t;
    }

    private LinearLayout card() {
        LinearLayout c = column();
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.setBackground(shape(PANEL, 20, Color.rgb(37, 49, 67)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(12);
        root.addView(c, lp);
        return c;
    }

    private void section(LinearLayout parent, String title, String trailing) {
        LinearLayout r = row();
        TextView a = text(title.toUpperCase(), 12, ACCENT, true);
        r.addView(a, new LinearLayout.LayoutParams(0, -2, 1));
        if (trailing != null) r.addView(text(trailing, 10, MUTED, false));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(22);
        parent.addView(r, lp);
    }

    private void buildHome() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        root = column();
        root.setPadding(dp(20), dp(20), dp(20), dp(28));
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout brandRow = row();
        TextView mark = text("S/", 20, ACCENT, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(shape(PANEL_ALT, 12, ACCENT));
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(dp(44), dp(44));
        brandRow.addView(mark, mp);
        LinearLayout brandCopy = column();
        brandCopy.setPadding(dp(12), 0, 0, 0);
        brandCopy.addView(text("STREETEMU", 14, TEXT, true));
        brandCopy.addView(text("NATIVE GAME PORT LAUNCHER", 9, MUTED, true));
        brandRow.addView(brandCopy);
        root.addView(brandRow);

        TextView headline = text("Your garage.\nYour settings.", 30, TEXT, true);
        headline.setLineSpacing(dp(1), 1.0f);
        LinearLayout.LayoutParams headLp = new LinearLayout.LayoutParams(-1, -2);
        headLp.topMargin = dp(26);
        root.addView(headline, headLp);
        TextView subtitle = text("A lightweight front end for a native Android game port.", 13, MUTED, false);
        subtitle.setPadding(0, dp(8), 0, 0);
        root.addView(subtitle);

        section(root, "Game profile", "NFS • UNDERGROUND 1");
        LinearLayout game = card();
        LinearLayout gameTop = row();
        TextView badge = text("NFS", 14, ACCENT, true);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(shape(PANEL_ALT, 14, Color.rgb(52, 91, 103)));
        gameTop.addView(badge, new LinearLayout.LayoutParams(dp(56), dp(56)));
        LinearLayout gameCopy = column();
        gameCopy.setPadding(dp(12), 0, 0, 0);
        gameCopy.addView(text("Need for Speed", 19, TEXT, true));
        gameCopy.addView(text("UNDERGROUND  •  ORIGINAL XBOX PORT", 9, MUTED, true));
        gameTop.addView(gameCopy, new LinearLayout.LayoutParams(0, -2, 1));
        game.addView(gameTop);
        addGap(game, 14);
        folderTitle = text("No game folder selected", 13, TEXT, true);
        game.addView(folderTitle);
        folderDetail = text("Choose the extracted game folder. Game data is not included in this app.", 11, MUTED, false);
        folderDetail.setPadding(0, dp(5), 0, dp(12));
        game.addView(folderDetail);
        game.addView(button("CHOOSE GAME FOLDER", true, this::pickFolder));
        TextView clear = text("Clear selected folder", 11, MUTED, true);
        clear.setGravity(Gravity.CENTER);
        clear.setPadding(0, dp(12), 0, 0);
        clear.setOnClickListener(v -> clearFolder());
        game.addView(clear);
        validationStatus = text("Folder validation has not run.", 11, MUTED, false);
        validationStatus.setPadding(0, dp(12), 0, 0);
        game.addView(validationStatus);

        section(root, "Runtime", "ENGINE STATUS");
        LinearLayout runtime = card();
        LinearLayout runtimeRow = row();
        TextView dot = text("!", 18, Color.rgb(255, 195, 96), true);
        dot.setGravity(Gravity.CENTER);
        dot.setBackground(shape(Color.rgb(63, 49, 28), 12, Color.rgb(115, 85, 42)));
        runtimeRow.addView(dot, new LinearLayout.LayoutParams(dp(42), dp(42)));
        LinearLayout runtimeCopy = column();
        runtimeCopy.setPadding(dp(12), 0, 0, 0);
        runtimeCopy.addView(text("Native engine not bundled", 14, TEXT, true));
        runtimeStatus = text("Launcher UI is ready; game execution is not connected in this build.", 11, MUTED, false);
        runtimeStatus.setPadding(0, dp(4), 0, 0);
        runtimeCopy.addView(runtimeStatus);
        runtimeRow.addView(runtimeCopy, new LinearLayout.LayoutParams(0, -2, 1));
        runtime.addView(runtimeRow);
        addGap(runtime, 14);
        runtime.addView(button("CHECK LAUNCH READINESS", false, this::checkLaunchReadiness));
        addGap(runtime, 8);
        runtime.addView(button("LAUNCH GAME", false, this::launchGame));

        section(root, "Performance profile", "SAVED ON DEVICE");
        LinearLayout settings = card();
        settings.addView(text("Tune the profile for your phone", 15, TEXT, true));
        TextView info = text("These options save your preferences. They affect game performance only after the native runtime is integrated.", 11, MUTED, false);
        info.setPadding(0, dp(5), 0, dp(12));
        settings.addView(info);
        addSetting(settings, "Quality preset", new String[]{"Battery saver", "Balanced", "Performance"}, "preset", "Balanced");
        addSetting(settings, "Internal resolution", new String[]{"50%", "75%", "100%", "125%", "150%"}, "resolution", "75%");
        addSetting(settings, "Frame-rate limit", new String[]{"30 FPS", "45 FPS", "60 FPS", "90 FPS", "120 FPS", "Uncapped"}, "fps", "60 FPS");
        addSetting(settings, "Touch controls", new String[]{"Standard", "Large buttons", "Minimal", "Off"}, "controls", "Standard");
        settingsSummary = text("", 11, ACCENT, true);
        settingsSummary.setPadding(0, dp(12), 0, 0);
        settings.addView(settingsSummary);

        section(root, "Device checks", "LOCAL INFO");
        LinearLayout device = card();
        device.addView(infoLine("Android", android.os.Build.VERSION.RELEASE + " (API " + android.os.Build.VERSION.SDK_INT + ")"));
        device.addView(infoLine("Architecture", android.os.Build.SUPPORTED_ABIS.length > 0 ? android.os.Build.SUPPORTED_ABIS[0] : "Unknown"));
        device.addView(infoLine("GPU / Vulkan", getVulkanSummary()));
        device.addView(infoLine("Port compatibility", getPortCompatibility()));
        TextView foot = text("StreetEmu • development build 0.2\nNo game files, BIOS, or copyrighted assets are bundled.", 10, MUTED, false);
        foot.setGravity(Gravity.CENTER);
        foot.setPadding(0, dp(24), 0, dp(8));
        root.addView(foot);
    }

    private View infoLine(String label, String value) {
        LinearLayout r = row();
        TextView left = text(label, 12, MUTED, false);
        TextView right = text(value, 12, TEXT, true);
        right.setGravity(Gravity.RIGHT);
        r.addView(left, new LinearLayout.LayoutParams(0, -2, 1));
        r.addView(right);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(7);
        r.setLayoutParams(lp);
        return r;
    }

    private void addSetting(LinearLayout parent, String title, String[] options, String key, String fallback) {
        TextView label = text(title, 12, MUTED, true);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(-1, -2);
        labelLp.topMargin = dp(10);
        parent.addView(label, labelLp);
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String saved = prefs.getString(key, fallback);
        int selected = 0;
        for (int i = 0; i < options.length; i++) if (options[i].equals(saved)) selected = i;
        spinner.setSelection(selected);
        spinner.setBackground(shape(PANEL_ALT, 12, Color.rgb(50, 65, 84)));
        spinner.setPadding(dp(8), dp(3), dp(8), dp(3));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parentView) { }
            @Override public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                prefs.edit().putString(key, options[position]).apply();
                updateSettingsSummary();
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(48));
        lp.topMargin = dp(5);
        parent.addView(spinner, lp);
    }

    private void updateSettingsSummary() {
        if (settingsSummary == null || prefs == null) return;
        settingsSummary.setText(prefs.getString("preset", "Balanced") + "  •  " +
                prefs.getString("resolution", "75%") + "  •  " + prefs.getString("fps", "60 FPS"));
    }

    private void pickFolder() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i, PICK_FOLDER);
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != PICK_FOLDER || result != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        try {
            int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException ignored) {
            toast("Folder selected, but Android could not persist access. Select it again if needed.");
        }
        selectedFolder = uri;
        prefs.edit().putString("gameFolder", uri.toString()).apply();
        validateSelectedFolder();
    }

    private void validateSelectedFolder() {
        if (selectedFolder == null) return;
        folderTitle.setText("Selected game folder");
        folderDetail.setText(selectedFolder.getLastPathSegment() == null ? selectedFolder.toString() : selectedFolder.getLastPathSegment());
        missingItems.clear();
        boolean hasXbe = false, hasCars = false, hasTracks = false;
        Cursor cursor = null;
        try {
            String treeId = DocumentsContract.getTreeDocumentId(selectedFolder);
            Uri children = DocumentsContract.buildChildDocumentsUriUsingTree(selectedFolder, treeId);
            cursor = getContentResolver().query(children,
                    new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE},
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String mime = cursor.getString(1);
                    if (name == null) continue;
                    String upper = name.toUpperCase(java.util.Locale.ROOT);
                    if (upper.equals("DEFAULT.XBE")) hasXbe = true;
                    if (upper.equals("CARS") && DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) hasCars = true;
                    if (upper.equals("TRACKS") && DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) hasTracks = true;
                }
            }
        } catch (Exception e) {
            missingItems.add("Could not read folder contents");
        } finally {
            if (cursor != null) cursor.close();
        }
        if (!hasXbe) missingItems.add("default.xbe");
        if (!hasCars) missingItems.add("CARS/");
        if (!hasTracks) missingItems.add("TRACKS/");
        folderValid = missingItems.isEmpty();
        if (folderValid) {
            validationStatus.setText("✓ Required top-level files found. This does not guarantee the game edition is compatible.");
            validationStatus.setTextColor(ACCENT);
        } else {
            validationStatus.setText("Missing or unreadable: " + join(missingItems) + ". Choose the extracted Xbox game folder.");
            validationStatus.setTextColor(Color.rgb(255, 190, 105));
        }
    }

    private String join(List<String> values) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) b.append(", ");
            b.append(values.get(i));
        }
        return b.toString();
    }

    private void clearFolder() {
        if (selectedFolder != null) {
            try { getContentResolver().releasePersistableUriPermission(selectedFolder,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION); }
            catch (Exception ignored) { }
        }
        selectedFolder = null;
        folderValid = false;
        prefs.edit().remove("gameFolder").apply();
        folderTitle.setText("No game folder selected");
        folderDetail.setText("Choose the extracted game folder. Game data is not included in this app.");
        validationStatus.setText("Folder selection cleared.");
        validationStatus.setTextColor(MUTED);
        toast("Game folder cleared.");
    }

    private void checkLaunchReadiness() {
        StringBuilder message = new StringBuilder();
        message.append("Folder: ").append(folderValid ? "required top-level files found" : "not validated").append("\n\n");
        message.append("Native runtime: not integrated in this StreetEmu source project.\n\n");
        message.append("Device ABI: ").append(android.os.Build.SUPPORTED_ABIS.length > 0 ? android.os.Build.SUPPORTED_ABIS[0] : "unknown").append("\n");
        message.append("Android: ").append(android.os.Build.VERSION.RELEASE).append(" / API ").append(android.os.Build.VERSION.SDK_INT).append("\n\n");
        message.append("Next engineering step: connect the compatible NFS Underground native runtime and verify the exact Xbox game edition.");
        new AlertDialog.Builder(this).setTitle("Launch readiness").setMessage(message.toString())
                .setPositiveButton("OK", null).show();
    }

    private void launchGame() {
        new AlertDialog.Builder(this)
                .setTitle("Runtime not connected")
                .setMessage("This build is the improved launcher foundation, not a playable game APK yet. The native NFS Underground engine and its JNI/SDL/Vulkan launch path must be integrated and tested before Launch Game can work. No game files are included.")
                .setPositiveButton("OK", null)
                .show();
    }


    private String getVulkanSummary() {
        if (android.os.Build.VERSION.SDK_INT < 24) return "Android API too old for Vulkan feature query";
        android.content.pm.PackageManager pm = getPackageManager();
        boolean level = pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL);
        boolean version = pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_VULKAN_HARDWARE_VERSION);
        if (level && version) return "Vulkan feature advertised; engine-level checks still required";
        if (level) return "Vulkan level advertised; version feature not reported";
        return "Vulkan hardware level not advertised";
    }

    private String getPortCompatibility() {
        boolean arm64 = false;
        for (String abi : android.os.Build.SUPPORTED_ABIS) {
            if ("arm64-v8a".equals(abi)) { arm64 = true; break; }
        }
        if (android.os.Build.VERSION.SDK_INT < 30) return "Android 11+ required by target port";
        if (!arm64) return "ARM64 device required by target port";
        return "Meets Android/API + ARM64 baseline; GPU/game edition still unverified";
    }

    private void toast(String message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
}
