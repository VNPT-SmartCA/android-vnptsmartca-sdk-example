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
                "eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCMkEyMEVDNzgwQzI5Nzk3QjE4RTAxQkIwMDNEQTEwNjM2RTlBMzJSUzI1NiIsInR5cCI6ImF0K2p3dCIsIng1dCI6IkN5b2c3SGdNS1hsN0dPQWJzQVBhRUdOdW1qSSJ9.eyJuYmYiOjE3MTY0NTgxMjYsImV4cCI6MTcxNjQ2MTcyNiwiaXNzIjoiVk5QVFJNX0lEUCIsImF1ZCI6WyJzaWduYXR1cmUiLCJWTlBUUk1fSURQL3Jlc291cmNlcyJdLCJjbGllbnRfaWQiOiI0MTg1LTYzNzEyNzk5NTU0NzMzMDYzMy5hcHBzLnNpZ25zZXJ2aWNlYXBpLmNvbSIsImNsaWVudF90cnVzdGVkM3JkIjoidHJ1ZSIsImNsaWVudF9uYW1lIjoiVk5QVCBTbWFydENBIEFwcCBEZW1vIiwiY2xpZW50X2FwcEZpZWxkIjoiIiwiY2xpZW50X2FwcFR5cGUiOiIiLCJzdWIiOiI3YTYxNTIwNS1hOGU4LTQ1ZjUtYmVlMi0wMTk1MjIwZTVlMDAiLCJhdXRoX3RpbWUiOjE3MTY0NTgxMjYsImlkcCI6ImxvY2FsIiwicm9sZSI6IlVzZXIiLCJGdWxsTmFtZSI6IlBI4bqgTSBWxIJOIFThu6giLCJDb21wYW55QWRtaW4iOiJDb21wYW55VXNlciIsIlVzZXJHcm91cElkIjoiIiwiVWlkIjoiMDM2MDkyMDA4NjEwIiwiUGhvbmVOdW1iZXIiOiIwODQ4MzQwMDg4IiwiQWNjb3VudFR5cGVEZXNjIjoiSU5ESVZJRFVBTCIsImxhbmd1YWdlIjoidmkiLCJTZXJ2aWNlUGFjayI6IiIsIkFkbWluTG9jYWxpdHlDb2RlIjoiIiwianRpIjoiMDZCODc2N0ZCMDJEQzFDQjQxQzFDNkVBMTAwMTU2NTUiLCJpYXQiOjE3MTY0NTgxMjYsInNjb3BlIjpbImVtYWlsIiwib3BlbmlkIiwicHJvZmlsZSIsInNpZ24iLCJvZmZsaW5lX2FjY2VzcyJdLCJhbXIiOlsiY3VzdG9tIl19.n5YqPjQ3rZ7ZDCK5I09UF3fAATEq7H2N0gDPagh1KSiAcoqXvEDiBqRp1gSCISkr0IqKGJKFOxFYywFiK1vhI73bZfWxCMf8GHyMBTqRr-mw9D0gudPYfu-7S_xDTsLZfmF7V8YgC2F_h78q1H9DRBEOFum2pVVlpqVLROnZNsE7Q6AK75awwyzF4IxaaHES7GmmhNnpLr3JC_Kk-qYn2Cj2m3iO_ZWuOPNP6Q2JcXWzY2K3U7fSG1oGs2v155KkgohXOMeD6DV3QGNBFibrSjpaHFv6Lxh308MXiEjUzCisfMkCqXHVAZgzaRJKi2A2nt_zV000Rm0NgQxTYftrzg"
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