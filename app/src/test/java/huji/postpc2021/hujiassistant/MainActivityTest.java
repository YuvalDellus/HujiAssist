package huji.postpc2021.hujiassistant;


import android.widget.Button;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import huji.postpc2021.hujiassistant.Activities.MainActivity;
import huji.postpc2021.hujiassistant.Fragments.FirstFragment;

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
public class MainActivityTest extends TestCase {
    HujiAssistentApplication app;
    MainActivity activityUnderTest;
    private Button exisitingUser;
    private Button newUser;

    @Before
    public void setup(){
        app = new HujiAssistentApplication();
        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class);
        activityUnderTest = activityController.get();
        activityController.create().start().resume();
    }

    @Test
    public void when_activityIsLaunching_then_InfoDrawerShouldBeClose(){
        DrawerLayout moreInfoDrawerLayout = activityUnderTest.findViewById(R.id.drawer_layout_more_info);
        assertFalse(moreInfoDrawerLayout.isDrawerOpen(GravityCompat.START));
    }

    @Test
    public void when_activityIsLaunching_then_FirstFragmentShouldBeActive(){
        FirstFragment firstFragment = (FirstFragment) activityUnderTest.getSupportFragmentManager().findFragmentByTag("FIRST_FRAGMENT");
        assertNotNull(firstFragment);
    }

    @Test
    public void when_activityIsLaunching_then_FirstFreagmentShouldBeActive(){
        exisitingUser = activityUnderTest.findViewById(R.id.exisitingUser);
        assertTrue(exisitingUser.isClickable());

        newUser = activityUnderTest.findViewById(R.id.newUser);
        assertTrue(newUser.isClickable());
    }
}
