package com.thxforservice.file.services;

import com.thxforservice.global.exceptions.BadRequestException;
import com.thxforservice.global.rests.ApiRequest;
import lombok.RequiredArgsConstructor;
import com.thxforservice.file.constants.FileStatus;
import com.thxforservice.file.entities.FileInfo;
import com.thxforservice.file.repositories.FileInfoRepository;
import org.springframework.stereotype.Service;
import com.thxforservice.global.Utils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileUploadDoneService {
    private final FileInfoRepository repository;
    private final FileInfoService infoService;
    private final ApiRequest apiRequest;
    private final Utils utils;

    public void process(String gid, String location) {

        List<FileInfo> items = infoService.getList(gid, location, FileStatus.ALL);
        items.forEach(i -> i.setDone(true));

        repository.saveAllAndFlush(items);
    }

    public void process(String gid) {
        ApiRequest result = apiRequest.request("/done/" + gid, "file-service");
        if (!result.getStatus().is2xxSuccessful()) {
            throw new BadRequestException(utils.getMessage("Fail.file.done"));
        }
    }
}
