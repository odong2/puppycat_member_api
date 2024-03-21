package com.architecture.admin.controllers.v1.certification;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.libraries.SecurityLibrary;
import com.architecture.admin.libraries.exception.CustomError;
import com.architecture.admin.libraries.exception.CustomException;
import com.architecture.admin.services.certification.CertificationService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/certification")
public class CertificationV1Controller extends BaseController {

    private final CertificationService certificationService;
    private final SecurityLibrary securityLibrary;

    /**
     * 회원가입 본인인증 팝업
     *
     * @return 본인인증 URL으로 이동
     * @throws Exception
     */
    @GetMapping("/popup")
    public ResponseEntity popup(@RequestParam("appKey") String appKey, @RequestParam("token") final String token) throws Exception {

        // appKey -> encrypt -> URLDecoder
        String aesKey = securityLibrary.aesEncrypt(appKey);

        // 비교 액션
        if (!aesKey.equals(token)) {
            throw new CustomException(CustomError.NOT_SAME_APP_KEY);
        }

        String popupUrl = certificationService.getUrl("join");

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", popupUrl);

        // return value
        String sErrorMessage = "lang.member.success.identity.verification";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }

    /**
     * 내 정보 핸드폰 번호 수정 시 본인인증 팝업
     *
     * @return 본인인증 URL으로 이동
     * @throws Exception
     */
    @GetMapping("/popup/my/info")
    public ResponseEntity popupMyinfo(@RequestParam("appKey") String appKey, @RequestParam("token") final String token) throws Exception {

        // appKey -> encrypt -> URLDecoder
        String aesKey = securityLibrary.aesEncrypt(appKey);

        // 비교 액션
        if (!aesKey.equals(token)) {
            throw new CustomException(CustomError.NOT_SAME_APP_KEY);
        }

        String popupUrl = certificationService.getUrl("my/info");

        // set return data
        JSONObject data = new JSONObject();
        data.put("location", popupUrl);

        // return value
        String sErrorMessage = "lang.member.success.identity.verification";
        String message = super.langMessage(sErrorMessage);
        return displayJson(true, "1000", message, data);
    }

    /**
     * 본인인증 리턴 url [인증완료 후 실행]
     *
     * @return kcb 토큰
     */
    @GetMapping("join")
    public String returnUrl(@RequestParam("mdl_tkn") String tkn) throws Exception {

        String bridge = "";

        JSONObject oInfo = certificationService.getInfo(tkn);

        if (!oInfo.get("RSLT_CD").equals("B000")) {

            JSONObject data = new JSONObject();
            data.put("code", "JCER-8888");
            data.put("lang", langMessage("lang.login.exception.certification.error"));

            bridge = "<script>\n" +
                    "\tisAppReady = false;\n" +
                    "function setPassAuthToken() {\n" +
                    "    if(isAppReady) {\n" +
                    "        window.flutter_inappwebview.callHandler('setErrorPassAuthToken', '" + data + "').then(function(result) {\n" +
                    "        });\n" +
                    "    }\n" +
                    "}\n" +
                    "\twindow.addEventListener(\"flutterInAppWebViewPlatformReady\", function(event) {\n" +
                    "\t    isAppReady = true;\n" +
                    "\t    setPassAuthToken();\n" +
                    "\t});\n" +
                    "\n" +
                    "</script>";

            return bridge;
        }

        // 이름 암호화
        String name = securityLibrary.aesEncrypt(oInfo.getString("RSLT_NAME"));
        //휴대폰번호 암호화
        String phone = securityLibrary.aesEncrypt(oInfo.getString("TEL_NO"));
        // 성별 암호화
        String gender = securityLibrary.aesEncrypt(oInfo.getString("RSLT_SEX_CD"));
        // 생일 암호화
        String birth = securityLibrary.aesEncrypt(oInfo.getString("RSLT_BIRTHDAY"));
        // ci 암호화
        String ci = securityLibrary.aesEncrypt(oInfo.getString("CI"));
        // di 암호화
        String di = securityLibrary.aesEncrypt(oInfo.getString("DI"));

        // set return data
        JSONObject data = new JSONObject();
        data.put("RSLT_NAME", name);
        data.put("TEL_NO", phone);
        data.put("RSLT_SEX_CD", gender);
        data.put("RSLT_BIRTHDAY", birth);
        data.put("CI", ci);
        data.put("DI", di);

         bridge = "<script>\n" +
                "\tisAppReady = false;\n" +
                "function setPassAuthToken() {\n" +
                "    if(isAppReady) {\n" +
                "        window.flutter_inappwebview.callHandler('setPassAuthToken', '" + data + "').then(function(result) {\n" +
                "        });\n" +
                "    }\n" +
                "}\n" +
                "\twindow.addEventListener(\"flutterInAppWebViewPlatformReady\", function(event) {\n" +
                "\t    isAppReady = true;\n" +
                "\t    setPassAuthToken();\n" +
                "\t});\n" +
                "\n" +
                "</script>";

        return bridge;
    }

    /**
     * 내 정보 수정 본인인증 리턴 url [인증완료 후 실행]
     *
     * @return kcb 토큰
     */
    @GetMapping("my/info")
    public String myInfoReturnUrl(@RequestParam("mdl_tkn") String tkn) throws Exception {

        String bridge = "";

        JSONObject oInfo = certificationService.getInfo(tkn);

        if (!oInfo.get("RSLT_CD").equals("B000")) {

            JSONObject data = new JSONObject();
            data.put("code", "JCER-8888");
            data.put("lang", langMessage("lang.login.exception.certification.error"));

            bridge = "<script>\n" +
                    "\tisAppReady = false;\n" +
                    "function setPassAuthToken() {\n" +
                    "    if(isAppReady) {\n" +
                    "        window.flutter_inappwebview.callHandler('setErrorPassAuthToken', '" + data + "').then(function(result) {\n" +
                    "        });\n" +
                    "    }\n" +
                    "}\n" +
                    "\twindow.addEventListener(\"flutterInAppWebViewPlatformReady\", function(event) {\n" +
                    "\t    isAppReady = true;\n" +
                    "\t    setPassAuthToken();\n" +
                    "\t});\n" +
                    "\n" +
                    "</script>";

            return bridge;
        }

        // 이름
        String name = oInfo.getString("RSLT_NAME");
        //휴대폰번호
        String phone = oInfo.getString("TEL_NO");
        // 성별 암호화
        String gender = securityLibrary.aesEncrypt(oInfo.getString("RSLT_SEX_CD"));
        // 생일 암호화
        String birth = securityLibrary.aesEncrypt(oInfo.getString("RSLT_BIRTHDAY"));
        // ci 암호화
        String ci = securityLibrary.aesEncrypt(oInfo.getString("CI"));
        // di 암호화
        String di = securityLibrary.aesEncrypt(oInfo.getString("DI"));

        // set return data
        JSONObject data = new JSONObject();
        data.put("RSLT_NAME", name);
        data.put("TEL_NO", phone);
        data.put("RSLT_SEX_CD", gender);
        data.put("RSLT_BIRTHDAY", birth);
        data.put("CI", ci);
        data.put("DI", di);

        bridge = "<script>\n" +
                "\tisAppReady = false;\n" +
                "function setPassAuthToken() {\n" +
                "    if(isAppReady) {\n" +
                "        window.flutter_inappwebview.callHandler('setPassAuthToken', '" + data + "').then(function(result) {\n" +
                "        });\n" +
                "    }\n" +
                "}\n" +
                "\twindow.addEventListener(\"flutterInAppWebViewPlatformReady\", function(event) {\n" +
                "\t    isAppReady = true;\n" +
                "\t    setPassAuthToken();\n" +
                "\t});\n" +
                "\n" +
                "</script>";

        return bridge;
    }
}
