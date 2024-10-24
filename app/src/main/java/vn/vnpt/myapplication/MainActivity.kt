package vn.vnpt.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.vnpt.smartca.ConfigSDK
import com.vnpt.smartca.CustomParams
import com.vnpt.smartca.SmartCAEnvironment
import com.vnpt.smartca.SmartCALanguage
import com.vnpt.smartca.SmartCAResultCode
import com.vnpt.smartca.VNPTSmartCASDK
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    var VNPTSmartCA = VNPTSmartCASDK()
    lateinit var editTextTrans: EditText;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val btnActiveAccount = findViewById<Button>(R.id.btnActiveAccount)
        editTextTrans = findViewById(R.id.plain_text_input)

        val btnConfirmTrans = findViewById<Button>(R.id.btnConfirmTrans)
        val bntMainInfo = findViewById<Button>(R.id.btnMainInfo)
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)


        var customParams = CustomParams(
//            customerId = "",
            borderRadiusBtn = 999.0,
            colorSecondBtn = "#DEF7EB",
            colorPrimaryBtn = "#33CC80",
            featuresLink = "https://www.google.com/?hl=vi",
//            customerPhone = "0916238782",
//            password = "",
            packageDefault = "PS0",
            logoCustom = "",
            backgroundLogin = ""
        )

        var config = ConfigSDK(
            clientId = "4185-637127995547330633.apps.signserviceapi.com",
            clientSecret = "NGNhMzdmOGE-OGM2Mi00MTg0",
            env = SmartCAEnvironment.DEMO_ENV,
            customParams = customParams,
            lang = SmartCALanguage.VI,
            isFlutter = false,
        )

        VNPTSmartCA.initSDK(this, config)

        btnActiveAccount.setOnClickListener {
            getAuthentication()
        }
        bntMainInfo.setOnClickListener {
            getMainInfo()
        }
        btnConfirmTrans.setOnClickListener {
            getWaitingTransaction(editTextTrans.text.toString())
        }
        btnSignOut.setOnClickListener {
            signOut()
        }
        btnCreateAccount.setOnClickListener {
            createAccount()
        }

    }
    private fun signOut() {
        try {
            VNPTSmartCA.signOut { result ->
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // Xử lý khi confirm thành công
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Thông báo")
                        builder.setMessage("Đăng xuất thành công")
                        builder.setPositiveButton(
                            "Close"
                        ) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }

                    else -> {
                        // Xử lý khi confirm thất bại
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            throw ex;
        }
    }

    private fun createAccount() {
        try {
            VNPTSmartCA.createAccount { result ->
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // Xử lý khi confirm thành công

                    }

                    else -> {
                        // Xử lý khi confirm thất bại
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            throw ex;
        }
    }

    private fun getMainInfo() {
        try {
            VNPTSmartCA.getMainInfo { result ->
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // Xử lý khi confirm thành công
                    }

                    else -> {
                        // Xử lý khi confirm thất bại
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            throw ex;
        }
    }

    private fun getAuthentication() {
        try {
            VNPTSmartCA.getAuthentication { result ->
                when (result.status) {
                    SmartCAResultCode.NO_EXIST_CERT_VALID -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Thông báo")
                        builder.setMessage("${result.data.toString()}")
                        builder.setPositiveButton(
                            "Close"
                        ) { dialog, _ ->
                            dialog.dismiss()
                            getMainInfo() }
                        val dialog = builder.create()
                        dialog.show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()
                            getMainInfo()
                        }, 5000)
                    }
                    SmartCAResultCode.SUCCESS_CODE -> {
                        val obj: CallbackResult = Json.decodeFromString<CallbackResult>(
                            result.data.toString()
                        )
                        // SDK trả lại token, credential của khách hàng
                        // Đối tác tạo transaction cho khách hàng để lấy transId, sau đó gọi getWaitingTransaction
                        val token = obj.accessToken
                        val credentialId = obj.credentialId

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Xác thực thành công")
                        builder.setMessage("CredentialId: $credentialId;\nAccessToken: $token")
                        builder.setPositiveButton(
                            "Close"
                        ) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }

                    else -> {
                        // Xử lý lỗi
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Thông báo")
                        builder.setMessage("status: ${result.status}; statusDesc:  ${result.statusDesc}")
                        builder.setPositiveButton(
                            "Close"
                        ) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }
                }
            }
        } catch (ex: Exception) {
            throw ex;
        }
    }

    private fun getWaitingTransaction(transId: String) {
        try {
            if (transId.isNullOrEmpty()) {
                editTextTrans.error = "Vui lòng điền Id giao dịch";
                return
            }

            VNPTSmartCA.getWaitingTransaction(transId,
            ) { result ->
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Thông báo")
                builder.setMessage("status: ${result.status}; statusDesc:  ${result.statusDesc}")
                builder.setPositiveButton(
                    "Close"
                ) { dialog, _ -> dialog.dismiss() }
                builder.show()

                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // Xử lý khi thành công
                    }

                    else -> {
                        // Xử lý khi thất bại
                    }
                }

            }
        } catch (ex: Exception) {
            throw ex;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VNPTSmartCA.destroySDK();
    }
}

@Serializable
data class CallbackResult(
    val credentialId: String,
    val accessToken: String,
) : java.io.Serializable