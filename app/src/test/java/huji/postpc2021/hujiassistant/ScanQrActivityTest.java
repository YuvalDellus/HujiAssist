package huji.postpc2021.hujiassistant;


import com.budiyev.android.codescanner.CodeScannerView;
import huji.postpc2021.hujiassistant.Activities.ScanQrActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ScanQrActivityTest extends TestCase {
    HujiAssistentApplication app;
    ScanQrActivity activityUnderTest;
    ActivityController<ScanQrActivity> activityController;

    @Before
    public void setup(){
        app = new HujiAssistentApplication();
        activityController = Robolectric.buildActivity(ScanQrActivity.class);
        activityUnderTest = activityController.get();
        activityController.create().start().resume();
    }

    @Test
    public void when_activityIsLaunching_then_FlashButtonShouldBeVisible(){
        CodeScannerView scannerView = activityUnderTest.findViewById(R.id.scanner_view);
        assertTrue(scannerView.isFlashButtonVisible());
    }

    @Test
    public void when_activityIsLaunching_then_AutoFocusButtonShouldBeVisible(){
        CodeScannerView scannerView = activityUnderTest.findViewById(R.id.scanner_view);
        assertTrue(scannerView.isAutoFocusButtonVisible());
    }

    @Test
    public void when_activityIsLaunching_then_CodeScannerShouldBeActive(){
        CodeScannerView scannerView = activityUnderTest.findViewById(R.id.scanner_view);
        assertFalse(activityUnderTest.isCodeScannerActive());
    }
}
