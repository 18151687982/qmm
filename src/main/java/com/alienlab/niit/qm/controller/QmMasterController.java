package com.alienlab.niit.qm.controller;

import com.alienlab.niit.qm.controller.util.ExecResult;
import com.alienlab.niit.qm.entity.*;
import com.alienlab.niit.qm.entity.dto.*;
import com.alienlab.niit.qm.repository.QmMasterListenRepository;
import com.alienlab.niit.qm.service.BaseTermService;
import com.alienlab.niit.qm.service.CourseService;
import com.alienlab.niit.qm.service.QmMasterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Master QB on 2017/5/5.
 */
@Api(value="/qm-api/masters",description = "督学API")
@RestController
@RequestMapping("/qm-api")
public class QmMasterController {
    @Autowired
    QmMasterService qmMasterService;
    @Autowired
    CourseService courseService;
    @Autowired
    BaseTermService baseTermService;
    @Autowired
    QmMasterListenRepository qmMasterListenRepository;


    @ApiOperation(value="根据督导工号和学期获得当前学期该督导所关注的教师列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "", response = BaseTeacherEntity.class),
            @ApiResponse(code = 500, message = "", response = ExecResult.class)
    })
    @GetMapping(value="/master")
    public ResponseEntity getCaredTeadcherByMasterNoAndTermNo(@RequestParam String masterNo,@RequestParam String termNo){
        try {
            List<BaseTeacherEntity> baseTeacherEntities = qmMasterService.findByMasterNoAndTerm(masterNo,termNo);

            return ResponseEntity.ok().body(baseTeacherEntities);
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="获得所关注教师的课表")
    @GetMapping(value="/master/caredteachercourse")
    public ResponseEntity getCaredTeadcherCourseList(@RequestParam String teacherNo,@RequestParam String termNo){
        try {
            List<CourseDetailDto> courseDetailDtos = qmMasterService.findByCaredTeacherNoAndTerm(teacherNo,termNo);
            return ResponseEntity.ok().body(courseDetailDtos);
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="获得堂教学质量评价表")
    @GetMapping(value="/master/rule")
    public ResponseEntity getRule(@RequestParam String rule_version_flag,@RequestParam String rule_table){
        try {
            List<QmRuleEntity> qmRuleEntities = qmMasterService.getQmRules(rule_version_flag,rule_table);
            return ResponseEntity.ok().body(qmRuleEntities);
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }


    @ApiOperation(value="获得督学常用语")
    @GetMapping(value="/master/config")
    public ResponseEntity getMasterConfig(@RequestParam String masterNo,@RequestParam String configType){
        try {
            List<QmMasterConfigEntity> qmMasterConfigEntities = qmMasterService.getConfigsByMasterNoAndType(masterNo,configType);
            return ResponseEntity.ok().body(qmMasterConfigEntities);
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }


    @ApiOperation(value="增加督学TK_JS评价")
    @PostMapping(value="/masterTKJSlisten")
    public ResponseEntity insertMasterListen(@RequestParam String ruleflag,@RequestParam String masterNo,@RequestParam long taskNo,@RequestParam int per11,@RequestParam int per12
            ,@RequestParam int per13,@RequestParam int per14,@RequestParam int per15,@RequestParam int per16,@RequestParam int total
            ,@RequestParam String jxjy,@RequestParam String tkpj,@RequestParam String listetime){
        QmMasterListenEntity qmMasterListenEntity = new QmMasterListenEntity();
        qmMasterListenEntity.setRuleFlag(ruleflag);
        qmMasterListenEntity.setTeacherNo(masterNo);
        qmMasterListenEntity.setTaskNo(taskNo);
        qmMasterListenEntity.setPer11(per11);
        qmMasterListenEntity.setPer12(per12);
        qmMasterListenEntity.setPer13(per13);
        qmMasterListenEntity.setPer14(per14);
        qmMasterListenEntity.setPer15(per15);
        qmMasterListenEntity.setPer16(per16);
        qmMasterListenEntity.setTotal(total);
        qmMasterListenEntity.setJxjy(jxjy);
        qmMasterListenEntity.setSkpj(tkpj);
        qmMasterListenEntity.setListenTime(Timestamp.valueOf(listetime));
        qmMasterListenEntity.setInputTime(new Timestamp(System.currentTimeMillis()));
        QmMasterListenEntity qmMasterListenEntity1 = qmMasterService.saveQmMasterListen(qmMasterListenEntity);
        if (qmMasterListenEntity1!=null){
            ExecResult right=  new ExecResult(true,"评价保存成功！");
            return ResponseEntity.ok().body(right);
        }else {
            ExecResult er=  new ExecResult(false,"评价保存保存失败！请重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }

    }

    @ApiOperation(value="获得督学TK_JS评价")
    @GetMapping(value="/masterTKJSlisten")
    public ResponseEntity getMasterListen(@RequestParam long listenplanNo){

        QmMasterListenEntity qmMasterListenEntity = qmMasterService.fingBylistenPlanNO(listenplanNo);
        if (qmMasterListenEntity!=null){
            return ResponseEntity.ok().body(qmMasterListenEntity);
        }else {
            ExecResult er=  new ExecResult(false,"评价获取失败！请重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }

    }

    @ApiOperation(value="修改督学评价")
    @PostMapping(value="/master/updatelisten")
    public ResponseEntity updateMasterListen(@RequestParam long planNo,@RequestParam int per11,@RequestParam int per12,@RequestParam int per13,@RequestParam int per14,@RequestParam int per15,@RequestParam int per16,@RequestParam int total,@RequestParam String jxjy,@RequestParam String tkpj,@RequestParam String listetime){
        QmMasterListenEntity qmMasterListenEntity =qmMasterListenRepository.findOne(planNo);
        qmMasterListenEntity.setPer11(per11);
        qmMasterListenEntity.setPer12(per12);
        qmMasterListenEntity.setPer13(per13);
        qmMasterListenEntity.setPer14(per14);
        qmMasterListenEntity.setPer15(per15);
        qmMasterListenEntity.setPer16(per16);
        qmMasterListenEntity.setTotal(total);
        qmMasterListenEntity.setJxjy(jxjy);
        qmMasterListenEntity.setSkpj(tkpj);
        qmMasterListenEntity.setListenTime(Timestamp.valueOf(listetime));
        qmMasterListenEntity.setInputTime(new Timestamp(System.currentTimeMillis()));
        QmMasterListenEntity qmMasterListenEntity1 = qmMasterService.saveQmMasterListen(qmMasterListenEntity);
        if (qmMasterListenEntity1!=null){
            ExecResult right=  new ExecResult(true,"评价修改成功！");
            return ResponseEntity.ok().body(right);
        }else {
            ExecResult er=  new ExecResult(false,"评价修改失败！请重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }

    }

    @ApiOperation(value="删除督学评价")
    @DeleteMapping(value="/master/deletelisten")
    public ResponseEntity deleteMasterListen(@RequestParam long listenNo){
       boolean flag =qmMasterService.deleteListenRecord(listenNo);
        if (flag){
            ExecResult right=  new ExecResult(true,"评价删除成功！");
            return ResponseEntity.ok().body(right);
        }else {
            ExecResult er=  new ExecResult(false,"评价删除失败！请重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }

    }


    @ApiOperation(value="按周次获得督学计划听课列表")
    @GetMapping(value="/master/listenplan")
    public ResponseEntity getMasterPlan(@RequestParam String masterNo,@RequestParam String termNo,@RequestParam String selectWeek){
        try {
            List<MasterPlanDto> masterPlanDtos = qmMasterService.getQmMasterListenPlan(termNo,masterNo,selectWeek);
            return ResponseEntity.ok().body(masterPlanDtos);
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }
    @ApiOperation(value="获得所有督学计划听课列表")
    @GetMapping(value="/master/listenplans")
    public ResponseEntity getAllMasterPlan(@RequestParam String masterNo,@RequestParam String termNo){
        try {
            List<MasterPlanDto> masterPlanDtos = qmMasterService.getAllQmMasterListenPlan(termNo,masterNo);
            return ResponseEntity.ok().body(masterPlanDtos);
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="修改督学计划听课列表")
    @PostMapping(value="/master/updatelistenplan")
    public ResponseEntity updateMasterPlan(@RequestParam long planNo,@RequestParam String listetime){
        try {
           boolean  flag = qmMasterService.updateListenPlan(planNo,listetime);
            if (flag){
                ExecResult right=  new ExecResult(true,"听课计划修改成功！");
                return ResponseEntity.ok().body(right);
            }else {
                ExecResult er=  new ExecResult(false,"听课计划修改失败！请重试");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="删除督学计划听课列表")
    @PostMapping(value="/master/deletelistenplan")
    public ResponseEntity deleteMasterPlan(@RequestParam long planNo){
        try {
            boolean  flag = qmMasterService.deleteListenPlan(planNo);
            if (flag){
                ExecResult right=  new ExecResult(true,"听课计划删除成功！");
                return ResponseEntity.ok().body(right);
            }else {
                ExecResult er=  new ExecResult(false,"听课计划删除失败！请重试");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,e.getMessage());
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="增加督学计划听课列表")
    @PostMapping(value="/master/addlistenplan")
    public ResponseEntity addMasterPlan(@RequestParam long taskNo,@RequestParam String termNo,@RequestParam String teacherNo,@RequestParam String plantime){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置显示格式
        Date dt=new Date();
        QmMasterListenPlanEntity qmMasterListenPlanEntity = new QmMasterListenPlanEntity();
        qmMasterListenPlanEntity.setTermNo(termNo);
        qmMasterListenPlanEntity.setTeacherNo(teacherNo);
        qmMasterListenPlanEntity.setTaskNo(taskNo);
        qmMasterListenPlanEntity.setPlanTime(java.sql.Date.valueOf(plantime));
        qmMasterListenPlanEntity.setPlanWeek(Long.toString(baseTermService.getSelectWeek(plantime)));
        qmMasterListenPlanEntity.setSetTime(java.sql.Date.valueOf(df.format(dt)));
        QmMasterListenPlanEntity qmMasterListenPlanEntity1 = qmMasterService.insertQmMasterListenPlan(qmMasterListenPlanEntity);
        if (qmMasterListenPlanEntity1!=null){
            return ResponseEntity.ok().body(qmMasterListenPlanEntity1);
        }else{
            ExecResult er=  new ExecResult(false,"听课计划新增失败！请重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="根据学期，教师工号返回课程")
    @GetMapping (value = "/master/termteachercourseDto")
    public ResponseEntity findCourseBytermNoAndteacherNo( @RequestParam String termNo,@RequestParam String teacherNo)  {

        try {
            List<CourseDto> courseDtos =courseService.findCourseByTermNoAndTeacherNo(termNo,teacherNo);
            if (courseDtos!=null){
                return ResponseEntity.ok().body(courseDtos);
            }else {
                ExecResult er=new ExecResult(false,"根据学期，教师工号返回课程失败！");
                //发生错误返回500状态
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,"系统出错，根据学期，教师工号返回课程失败！");
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="督学根据教师姓名查询本部门的教师名称")
    @GetMapping (value = "/master/teacherDto")
    public ResponseEntity findTeachrByMasterNo( @RequestParam String keyword,@RequestParam String masterNo, @RequestParam String termNo,@RequestParam int index,@RequestParam int length)  {

        try {
            Page<TeacherDto> teacherDtos = qmMasterService.findByMasterNoAndTermNoAndKeyword(keyword,masterNo,termNo,new PageRequest(index,length));
            if (teacherDtos!=null){
                return ResponseEntity.ok().body(teacherDtos);
            }else {
                ExecResult er=new ExecResult(false,"督学根据教师姓名查询本部门的教师名称失败！");
                //发生错误返回500状态
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,"系统出错，督学根据教师姓名查询本部门的教师名称失败！");
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="督导根据周几获取本部门的教师课程信息")
    @GetMapping (value = "/master/daycourse")
    public ResponseEntity findWeekCourseByMasterNoAndTermNoAndKeyword( @RequestParam String keyword,@RequestParam String masterNo, @RequestParam String termNo,@RequestParam int index,@RequestParam int length)  {

        try {
            Page<CourseDetailDto> courseDetailDtos = qmMasterService.findCourseByMasterNoAndTermNoAndKeyword(keyword,masterNo,termNo,new PageRequest(index,length));
            if (courseDetailDtos!=null){
                return ResponseEntity.ok().body(courseDetailDtos);
            }else {
                ExecResult er=new ExecResult(false,"督学根据周次姓名查询本部门的教师课程失败！");
                //发生错误返回500状态
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,"系统出错，督学根据周次姓名查询本部门的教师课程失败！");
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }

    @ApiOperation(value="获取督导听课记录")
    @GetMapping (value = "/master/listenPlanDto")
    public ResponseEntity findMasterListenPlan( @RequestParam String masterNo,@RequestParam String termNo, @RequestParam String teracherName,@RequestParam String startTime,@RequestParam String endTime)  {

        try {
            List <ListenPlanDto> listenPlanDtos = qmMasterService.findMasterListenPlan(masterNo, termNo, teracherName, startTime, endTime);
            if (listenPlanDtos!=null){
                return ResponseEntity.ok().body(listenPlanDtos);
            }else {
                ExecResult er=new ExecResult(false,"督学根据教师姓名查询本部门的教师名称失败！");
                //发生错误返回500状态
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExecResult er=new ExecResult(false,"系统出错，督学根据教师姓名查询本部门的教师名称失败！");
            //发生错误返回500状态
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
        }
    }


}
