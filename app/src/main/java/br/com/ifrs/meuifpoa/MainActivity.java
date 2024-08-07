package br.com.ifrs.meuifpoa;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private boolean isAuthDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        setupToolbar();
        configFirebaseAuth();
        configNavegacao();
        configBottomNavigationView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void configFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void configNavegacao() {
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(destination.getLabel());
            }
        });
    }

    private void configBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.getMenu().removeItem(R.id.Sobre);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.notasFragment) {
                if (isUsuarioAutenticado()) {
                    navController.navigate(R.id.notasFragment);
                } else {
                    startIntentLoginActivity();
                }
                return true;
            } else if (itemId == R.id.noticiasFragment) {
                navController.navigate(R.id.noticiasFragment);
                return true;
            } else if (itemId == R.id.perfilFragment) {
                if (isUsuarioAutenticado()) {
                    navController.navigate(R.id.perfilFragment);
                } else {
                    startIntentLoginActivity();
                }
                return true;
            } else if (itemId == R.id.homeFragment) {
                navController.navigate(R.id.homeFragment);
                return true;
            }
            return super.onOptionsItemSelected(item);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.getGraph().getStartDestination(), false)
                .build();

        int itemId = item.getItemId();
        if (itemId == R.id.notasFragment) {
            if (isUsuarioAutenticado()) {
                navController.navigate(R.id.notasFragment, null, navOptions);
            } else {
                startIntentLoginActivity();
            }
            return true;
        } else if (itemId == R.id.noticiasFragment) {
            navController.navigate(R.id.noticiasFragment, null, navOptions);
            return true;
        } else if (itemId == R.id.perfilFragment) {
            if (isUsuarioAutenticado()) {
                navController.navigate(R.id.perfilFragment, null, navOptions);
            } else {
                startIntentLoginActivity();
            }
            return true;
        } else if (itemId == R.id.Sobre) {
            showSobreDialog();
            return true;
        } else if (itemId == R.id.homeFragment) {
            navController.navigate(R.id.homeFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private boolean isUsuarioAutenticado() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return true;
    }

    private void startIntentLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void showSobreDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.msg_titulo_sobre)
                .setIcon(R.drawable.ifrs_poa_logo)
                .setMessage(R.string.msg_sobre)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
