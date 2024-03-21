package com.architecture.admin.controllers.v1.search;

import com.architecture.admin.controllers.v1.BaseController;
import com.architecture.admin.services.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/search")
public class SearchV1Controller extends BaseController {

    private final SearchService searchService;


    @GetMapping("/nick")
    public ResponseEntity getSearchNickUuid(@RequestParam(name = "searchWord") String searchWord) {

        List<String> searchMemberUuidList = searchService.getSearchNickUuid(searchWord);
        if (searchMemberUuidList == null || searchMemberUuidList.isEmpty()) {
            searchMemberUuidList = new ArrayList<>();
        }

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("searchMemberUuidList", searchMemberUuidList);

        return displayJson(true, "1000", sMessage, data);
    }

    @GetMapping("/nick/same")
    public ResponseEntity getSameNickUuid(@RequestParam(name = "searchWord") String searchWord) {

        String searchMemberUuid = searchService.getSameNickUuid(searchWord);
        if (searchMemberUuid == null) {
            searchMemberUuid = "";
        }

        String sMessage = super.langMessage("lang.common.success.search");

        JSONObject data = new JSONObject();
        data.put("searchMemberUuid", searchMemberUuid);

        return displayJson(true, "1000", sMessage, data);
    }
}
