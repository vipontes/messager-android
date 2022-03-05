package easify.mess.view

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import easify.mess.R
import easify.mess.databinding.ActivityLoginBinding
import easify.mess.model.LoginBody
import easify.mess.model.Response
import easify.mess.model.User
import easify.mess.viewmodel.LoginViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginBody: LoginBody
    private lateinit var dataBinding: ActivityLoginBinding

    private val loginObserver = Observer<LoginBody> { data: LoginBody ->
        data.let {
            loginBody = it
            dataBinding.loginBody = loginBody
        }
    }

    private val userObserver = Observer<User> {
        if (it != null) {
            startMainActivity()
            finish()
        }
    }

    private val errorMessageObserver = Observer<Response> { error: Response ->
        error.let {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            dataBinding.spinner.visibility = View.GONE
            dataBinding.loginButton.visibility = View.VISIBLE
        }
    }

    private fun startMainActivity() {
        val mainActivity = Intent(this, HomeActivity::class.java)
        startActivity(mainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()

        requestPermissions()
        initializeDataBinding()
    }

    private fun requestPermissions() {
        Dexter.withActivity(this@LoginActivity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.VIBRATE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {

                }
            }).check()
    }

    private fun initializeDataBinding() {
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        dataBinding.loginButton.setOnClickListener(View.OnClickListener {
            if (viewModel.validate()) {
                dataBinding.spinner.visibility = View.VISIBLE
                dataBinding.loginButton.visibility = View.GONE
                viewModel.login()
            }
        })
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.loginBody.observe(this, loginObserver)
        viewModel.userData.observe(this, userObserver)
        viewModel.errorResponse.observe(this, errorMessageObserver)
    }
}