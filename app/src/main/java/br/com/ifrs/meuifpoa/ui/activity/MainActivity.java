package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.ActivityMainBinding;

/**
 * MainActivity gerencia a interface principal do aplicativo, incluindo navegação entre fragments,
 * autenticação com Firebase e a barra de navegação inferior.
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private NavController navController;
    private ActivityMainBinding binding;

    /**
     * Método chamado quando a atividade é criada.
     *
     * @param savedInstanceState Estado salvo da atividade, se disponível.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o Firebase
        FirebaseApp.initializeApp(this);

        // Configura a MaterialToolbar como a ActionBar
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Configura FirebaseAuth, Navegação e BottomNavigationView
        configFirebaseAuth();
        configNavegacao();
        configBottomNavigationView();

        // Configura a navegação inicial com base no Intent recebido
        Intent intent = getIntent();
        if (intent != null) {
            int selectedItemId = intent.getIntExtra("selectedItemId", R.id.homeFragment);
            binding.bottomNav.setSelectedItemId(selectedItemId);
        }
    }

    /**
     * Configura a autenticação com Firebase.
     */
    private void configFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Configura a navegação do aplicativo usando o NavController.
     */
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

    /**
     * Configura o BottomNavigationView para permitir navegação entre fragments.
     */
    private void configBottomNavigationView() {
        // Remove o item "Sobre" do menu
        binding.bottomNav.getMenu().removeItem(R.id.Sobre);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        // Define listener para navegação na barra inferior
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
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

    /**
     * Cria o menu de opções na barra superior.
     *
     * @param menu O menu a ser inflado.
     * @return true se o menu foi criado com sucesso.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Trata a seleção de itens no menu de opções.
     *
     * @param item O item selecionado.
     * @return true se a ação foi tratada, caso contrário, o comportamento padrão.
     */
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

    /**
     * Trata a navegação para cima na hierarquia de navegação.
     *
     * @return true se a navegação foi bem-sucedida, caso contrário, o comportamento padrão.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * Verifica se o usuário está autenticado.
     *
     * @return true se o usuário estiver autenticado, false caso contrário.
     */
    private boolean isUsuarioAutenticado() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    /**
     * Inicia a LoginActivity para que o usuário faça login.
     */
    private void startIntentLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Exibe um diálogo "Sobre" com informações sobre o aplicativo.
     */
    private void showSobreDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.msg_titulo_sobre)
                .setIcon(R.drawable.ifrs_poa_logo)
                .setMessage(R.string.msg_sobre)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
