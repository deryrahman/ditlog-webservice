package id.ac.itb.logistik.ditlog.controller;

import id.ac.itb.logistik.ditlog.model.*;
import id.ac.itb.logistik.ditlog.repository.IndicatorRepository;
import id.ac.itb.logistik.ditlog.repository.PenilaianRepository;
import id.ac.itb.logistik.ditlog.repository.SPMKContractRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class PenilaianController {

    @Autowired
    SPMKContractRepository spmkContractRepository;
    @Autowired
    PenilaianRepository penilaianRepository;
    @Autowired
    IndicatorRepository indicatorRepository;
    Map<Long,String> ROLE = RoleConstant.ROLE;
    Map<Long,String> TAG = RoleConstant.TAG;

    @GetMapping("/contracts/{id}/indicators")
    public ResponseEntity<BaseResponse> getIndicatorByIdContract(
            HttpServletRequest request,
            @PathVariable("id") Long id
    ) throws NotFoundException {
        BaseResponse baseResponse = new BaseResponse();
        User user = (User) request.getAttribute("user");
        Long roleId = user.getIdResponsibility();
        SPMKContract contract = spmkContractRepository.findContractById(id);
        if(!ROLE.get(roleId).equals("KASUBDIT_PEMERIKSA") && !contract.getJenis().equals(TAG.get(roleId))){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        if(contract == null){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        Iterable<PenilaianKinerja> penilaianKinerjaIterable = penilaianRepository.findAllByIdContract(id);
        if(penilaianKinerjaIterable.spliterator().getExactSizeIfKnown() == 0){
            throw new EntityNotFoundException(PenilaianKinerja.class.getSimpleName());
        }
        List<PenilaianKinerja> penilaianKinerjaList = new ArrayList<>();
        for(PenilaianKinerja penilaianKinerja:penilaianKinerjaIterable) {
            penilaianKinerja.setIdIndicator();
            Indicator indicator = indicatorRepository.findOne(penilaianKinerja.penilaianIdentity.getIdIndikator());
            if(indicator == null){
                throw new NotFoundException(Indicator.class.getSimpleName());
            }
            penilaianKinerja.setNamaIndikator(indicator.getName());
            penilaianKinerjaList.add(penilaianKinerja);
        }
        baseResponse.setStatus(true);
        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setPayload(penilaianKinerjaList);

        return ResponseEntity.ok(baseResponse);
    }


    @PostMapping("/contracts/{id}/indicators")
    public ResponseEntity<BaseResponse> addIndicatorOnContract(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody ArrayList<Long> indicatorIdList
    ){
        BaseResponse baseResponse = new BaseResponse();
        User user = (User) request.getAttribute("user");
        Long roleId = user.getIdResponsibility();
        SPMKContract contract = spmkContractRepository.findContractById(id);
        if(!ROLE.get(roleId).equals("KASUBDIT_PEMERIKSA") && !contract.getJenis().equals(TAG.get(roleId))){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        if(contract == null){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        List<PenilaianKinerja> penilaianKinerjaList = new ArrayList<>();
        String msg = "Some indicators has been added before. These id are : ";
        String msg2 = "Some indicators may not exist. These id are : ";
        boolean msg_flag = false, msg2_flag = false;
        for (Long indicatorId:indicatorIdList) {
            PenilaianKinerja penilaianKinerja = penilaianRepository.findOne(new PenilaianIdentity(id, indicatorId));
            boolean isExist = penilaianKinerja != null;
            if (isExist) {
                msg_flag = true;
                msg += indicatorId.toString() + " ";
            }
            penilaianKinerja = new PenilaianKinerja(new PenilaianIdentity(id, indicatorId));
            Indicator indicator = indicatorRepository.findOne(penilaianKinerja.penilaianIdentity.getIdIndikator());
            if (indicator == null) {
                msg2_flag = true;
                msg2 += indicatorId.toString() + " ";
            }
            if (isExist) continue;
            penilaianKinerja.setNamaIndikator(indicator.getName());
            penilaianKinerjaList.add(penilaianKinerja);
            penilaianRepository.save(penilaianKinerja);
        }
        baseResponse.setStatus(true);
        baseResponse.setCode(HttpStatus.OK.value());
        String msg_final = "";
        if(msg_flag) msg_final += msg;
        if(msg2_flag) msg_final += ". " + msg2;
        if(msg_flag || msg2_flag) baseResponse.setMessage(msg_final);
        baseResponse.setPayload(penilaianKinerjaList);

        return ResponseEntity.ok(baseResponse);
    }


    @PutMapping("/contracts/{id}/indicators")
    public ResponseEntity<BaseResponse> updateIndicatorOnContract(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody ArrayList<PenilaianSimplified> penilaianList
    ) {
        BaseResponse baseResponse = new BaseResponse();
        User user = (User) request.getAttribute("user");
        Long roleId = user.getIdResponsibility();
        SPMKContract contract = spmkContractRepository.findContractById(id);
        if(!ROLE.get(roleId).equals("KASUBDIT_PEMERIKSA") && !contract.getJenis().equals(TAG.get(roleId))){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        if(contract == null){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        List<PenilaianKinerja> penilaianKinerjaUpdateList = new ArrayList<>();
        for (PenilaianSimplified penilaian : penilaianList) {
            PenilaianKinerja penilaianKinerjaUpdate = penilaianRepository.findOne(new PenilaianIdentity(id,penilaian.getIdIndikator()));
            if(penilaianKinerjaUpdate == null){
                throw new EntityNotFoundException(PenilaianKinerja.class.getSimpleName());
            }
            penilaianKinerjaUpdate.setNilai(penilaian.getNilai());
            penilaianKinerjaUpdate.setIdIndicator();
            Indicator indicator = indicatorRepository.findOne(penilaian.getIdIndikator());
            if(indicator == null){
                throw new EntityNotFoundException(Indicator.class.getSimpleName());
            }
            penilaianKinerjaUpdate.setNamaIndikator(indicator.getName());
            penilaianKinerjaUpdateList.add(penilaianKinerjaUpdate);
        }
        penilaianRepository.save(penilaianKinerjaUpdateList);
        baseResponse.setStatus(true);
        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setPayload(penilaianKinerjaUpdateList);

        return ResponseEntity.ok(baseResponse);
    }


    @DeleteMapping("/contracts/{id}/indicators")
    public ResponseEntity<BaseResponse> deleteIndicatorListOnContract(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody ArrayList<Long> indicatorIdList
    ){
        BaseResponse baseResponse = new BaseResponse();
        User user = (User) request.getAttribute("user");
        Long roleId = user.getIdResponsibility();
        SPMKContract contract = spmkContractRepository.findContractById(id);
        if(!ROLE.get(roleId).equals("KASUBDIT_PEMERIKSA") && !contract.getJenis().equals(TAG.get(roleId))){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        if(contract == null){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        String msg = "Some penilaian has been deleted. These id are : ";
        String msg2 = "Some indicator couldn't seen in db. These id are : ";
        boolean msg_flag = false, msg2_flag = false;
        List<PenilaianKinerja> penilaianKinerjaList = new ArrayList<>();
        for(Long indicatorId:indicatorIdList){
            PenilaianIdentity penilaianIdentity = new PenilaianIdentity(id,indicatorId);
            PenilaianKinerja penilaianKinerja = penilaianRepository.findOne(penilaianIdentity);
            if(penilaianKinerja == null){
                msg_flag = true;
                msg += indicatorId.toString() + " ";
                continue;
            }
            Indicator indicator = indicatorRepository.findOne(indicatorId);
            if(indicator == null){
                msg2_flag = true;
                msg2 += indicatorId.toString() + " ";
                continue;
            }
            penilaianKinerja.setIdIndicator();
            penilaianKinerja.setNamaIndikator(indicator.getName());
            penilaianKinerjaList.add(penilaianKinerja);
            penilaianRepository.delete(penilaianIdentity);
        }
        if(penilaianKinerjaList.isEmpty()){
            throw new EntityNotFoundException(PenilaianKinerja.class.getSimpleName());
        }
        baseResponse.setStatus(true);
        baseResponse.setCode(HttpStatus.OK.value());
        String msg_final = "";
        if(msg_flag) msg_final += msg;
        if(msg2_flag) msg_final += ". " + msg2;
        if(msg_flag || msg2_flag) baseResponse.setMessage(msg_final);
        baseResponse.setPayload(penilaianKinerjaList);

        return ResponseEntity.ok(baseResponse);
    }

    @DeleteMapping("/contracts/{id}/indicators/{indicatorId}")
    public ResponseEntity<BaseResponse> deleteIndicatorOnContract(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @PathVariable("indicatorId") Long indicatorId
    ){
        BaseResponse baseResponse = new BaseResponse();
        User user = (User) request.getAttribute("user");
        Long roleId = user.getIdResponsibility();
        SPMKContract contract = spmkContractRepository.findContractById(id);
        if(!ROLE.get(roleId).equals("KASUBDIT_PEMERIKSA") && !contract.getJenis().equals(TAG.get(roleId))){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        if(contract == null){
            throw new EntityNotFoundException(SPMKContract.class.getSimpleName());
        }
        PenilaianIdentity penilaianIdentity = new PenilaianIdentity(id,indicatorId);
        PenilaianKinerja penilaianKinerja = penilaianRepository.findOne(penilaianIdentity);
        if(penilaianKinerja == null){
            throw new EntityNotFoundException(PenilaianKinerja.class.getSimpleName());
        }
        Indicator indicator = indicatorRepository.findOne(penilaianIdentity.getIdIndikator());
        if(indicator == null){
            throw new EntityNotFoundException(Indicator.class.getSimpleName());
        }
        penilaianRepository.delete(penilaianIdentity);
        penilaianKinerja.setIdIndicator();
        penilaianKinerja.setNamaIndikator(indicator.getName());
        baseResponse.setStatus(true);
        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setPayload(penilaianKinerja);

        return ResponseEntity.ok(baseResponse);
    }
}
