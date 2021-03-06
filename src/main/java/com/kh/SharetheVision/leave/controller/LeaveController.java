package com.kh.SharetheVision.leave.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kh.SharetheVision.approval.model.service.ApprovalService;
import com.kh.SharetheVision.approval.model.vo.ApprovalAcceptDTO;
import com.kh.SharetheVision.approval.model.vo.ApprovalVO;
import com.kh.SharetheVision.leave.model.exception.LeaveException;
import com.kh.SharetheVision.leave.model.service.LeaveService;
import com.kh.SharetheVision.leave.model.vo.LeaveAnnual;
import com.kh.SharetheVision.leave.model.vo.LeaveUsed;
import com.kh.SharetheVision.member.model.service.MemberService;
import com.kh.SharetheVision.member.model.vo.Member;

import au.com.bytecode.opencsv.CSVReader;

@Controller
public class LeaveController {
	
	@Autowired
	private LeaveService leService;
	
	@Autowired
	private MemberService mService;
	
	@Autowired
	private ApprovalService apvService; 
	
	@RequestMapping("leaveDetail.le")
	public String leaveDetailView(HttpSession session, Model model) {
		Member loginUser = ((Member)session.getAttribute("loginUser"));
		String memberNo = loginUser.getmCode();
		String name = loginUser.getName();
		String jobName = loginUser.getJobName();
		String deptName = loginUser.getDeptName();
		
		model.addAttribute("name", name);
		model.addAttribute("jobName", jobName);
		model.addAttribute("deptName", deptName);
		
		// ?????? ??????
		ArrayList<LeaveAnnual> annualList = leService.selectAnnual(memberNo);
		if(annualList != null) {
			Collections.reverse(annualList);
			model.addAttribute("annualList", annualList);

			double annualTotal = 0;
			for(LeaveAnnual la : annualList) {
				if(la.getType() == 0) {
					annualTotal += la.getTotal();
				}
			}
			model.addAttribute("annualTotal", annualTotal);

			double adjustTotal = 0;
			for(LeaveAnnual la : annualList) {
				if(la.getType() == 1) {
					adjustTotal += la.getTotal();
				}
			}
			model.addAttribute("adjustTotal", adjustTotal);
		}
		
		// ?????? ??????
		ArrayList<LeaveUsed> leaveList = leService.selectLeave(memberNo);
		if(leaveList != null) {
			Collections.reverse(leaveList);
			model.addAttribute("leaveList", leaveList);
			
			double usedTotal = 0;
			for(LeaveUsed lu : leaveList) {
				if(lu.getStatus().charAt(0) == 'Y') {
					usedTotal += lu.getDays();
				}
			}
			model.addAttribute("usedTotal", usedTotal);
		}
		
//		System.out.println("???????????? : " + annualList);
//		System.out.println("???????????? : " + leaveList);
		
		// ?????? ?????????
		Member m = new Member();
		m.setmCode(memberNo);
		
		ArrayList<Member> mlist = mService.selectMemberList(m);
		
		if(mlist != null) {
			model.addAttribute("mlist", mlist);
		}
		
		return "leaveDetailView";
	}
	
	@RequestMapping("leaveSetting.le")
	public String leaveSetting(@RequestParam("uploadFile") MultipartFile file, HttpServletRequest request) throws LeaveException {
		
        LeaveAnnual la = null;
        int result = 0;
        int size = 0;
        
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		cal.setTime(new Date(System.currentTimeMillis()));
		int year = cal.get(Calendar.YEAR);
		
		String savePath = request.getSession().getServletContext().getRealPath("resources") + "/leaveSetting/setting.csv";
		
		File csv = new File(savePath);
		if(!csv.exists()) {
			csv.mkdirs();
		}
		
		try {
			file.transferTo(new File(savePath));
			CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(savePath), "utf-8"));
			
			List<String[]> data = new ArrayList<String[]>();
            
            data = csvReader.readAll();
            
            ArrayList<LeaveAnnual> list = new ArrayList<LeaveAnnual>();
        	for(int i = 0; i < data.size()-1; i++) {
        		String[] line = data.get(i+1);
        		
        		if(line[0].isEmpty()) {
    				break;
    			}
        		
        		la = new LeaveAnnual();
        		la.setYear(Integer.toString(year));
        		
            	for(int j = 0; j < line.length; j++) {
            		if(j == 0) {
            			// ?????? ?????????
            			Member member = mService.loginMember(line[j].toString());
//            			System.out.println(line[j]);
            			la.setMemberId(line[j]);
            			la.setMemberNo(member.getmCode());
            		} else if(j == 1) {
            			// ?????????
            			
//            			System.out.println(line[j]);
            			String base = line[j].trim();
            			la.setBaseDate(base);
            			la.setStartDate(year + base.substring(5));
            			la.setEndDate((year+1) + base.substring(5));
            		} else if(j == 2) {
            			// ??? ?????? ??????
            			
//            			System.out.println(line[j]);
            			la.setTotal(Integer.parseInt(line[j]));
            		} else if(j == 3) {
            			// ??????
            			
            			la.setContent(line[j]);
            		} else if(j == 4) {
            			// ?????? ??????
//            			System.out.println(line[j]);
            			if(line[j].trim().equals("????????????")) {
            				la.setType(0);
            			} else if(line[j].trim().equals("????????????")) {
            				la.setType(1);
            			} else {
            				throw new LeaveException("?????? ????????? ?????????????????????. ??????????????? ??????????????????.");
            			}
            		}
            	}
//            	System.out.println(la);
            	list.add(la);
            }
//        	System.out.println(list);
        	size = list.size();
        	result = leService.insertAnnual(list);
        	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(result >= size) {
			return "redirect: leaveDetail.le";
		} else {
			throw new LeaveException("?????? ?????? ????????? ?????????????????????.");
		}
	}
	
	@ResponseBody
	@RequestMapping("leaveRequest.le")
	public String leaveRequest(@ModelAttribute LeaveUsed lu, @RequestParam("approval") String approval, HttpSession session) throws LeaveException {
		Member loginUser = ((Member)session.getAttribute("loginUser"));
		String memberNo = loginUser.getmCode();
//		String memberNo = "MaCo2";
		
		lu.setMemberNo(memberNo);
		
		int result = leService.insertLeave(lu);
		int no = lu.getLeaveUsedNo();
//		System.out.println(no + " : ????????? ??????");
		
		int apvResult = 0;
		int acceptResult = 0;
		if(result > 0) {
			ApprovalVO apv = new ApprovalVO();
			apv.setApvType("??????");
			apv.setmCode(memberNo);
			apv.setApvApp(approval);
			apv.setApt("?????? ?????????");
			
			String type = null;
			if(lu.getType() == 1) {
				type = "??????";
			} else if(lu.getType() == 3){
				type = "?????????";
			} else if(lu.getType() == 4){
				type="??????";
			} else if(lu.getType() == 5){
				type="??????";
			} else if(lu.getType() == 6){
				type="????????????";
			} else if(lu.getType() == 7){
				type="????????????";
			} else if(lu.getType() == 8){
				type="????????????";
			}
			apv.setComment("???????????? : " + type +
						  "\r\n????????? : " + lu.getStartDate() +
						  "\r\n????????? : " + lu.getEndDate() +
						  "\r\n??????  : " + lu.getDays() +
						  "\r\n?????? : " + lu.getContent());
			apv.setApvRefNo(no);
			
			long miliseconds = System.currentTimeMillis();
			Date arrive = new Date(miliseconds);
			Date depart = Date.valueOf(lu.getStartDate());
			apv.setArrive(arrive);
			apv.setDepart(depart);

			apvResult = apvService.insertApproval(apv);
			
			if(apvResult > 0) {
				String[] appArr = apv.getApvApp().split(",");
				for (String app : appArr) {
					acceptResult = apvService.insertApprovalAccept(new ApprovalAcceptDTO(apv.getApvNo(), app, "APP"));
				}
			}
		}
		
		if(acceptResult > 0) {
			return "success";
		} else {
			throw new LeaveException("?????? ????????? ?????????????????????.");
		}
	}
	
	@ResponseBody
	@RequestMapping(value="leaveList.le", produces="application/json; charset=utf-8")
	public String LeaveTable(HttpSession session) {
		Member loginUser = ((Member)session.getAttribute("loginUser"));
		String memberNo = loginUser.getmCode();
//		String memberNo = "MaCo2";
		
		ArrayList<LeaveUsed> list = leService.selectLeave(memberNo);
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("list", list);
		
		return gson.toJson(map);
	}
	
	@RequestMapping("changeStatus.le")
	public String changeStatus(@RequestParam("status") String status, @RequestParam("no") int no, HttpSession session) throws LeaveException {
		Member loginUser = ((Member)session.getAttribute("loginUser"));
		String memberNo = loginUser.getmCode();
//		String memberNo = "MaCo2";
		
		LeaveUsed lu = new LeaveUsed();
		lu.setMemberNo(memberNo);
		lu.setLeaveUsedNo(no);
		lu.setStatus(status);
		
		int result = leService.changeStatus(lu);
//		System.out.println(result + " : ??????");
		
		if(result > 0) {
			return "redirect: leaveDetail.le";
		} else {
			throw new LeaveException("?????? ????????? ?????????????????????.");
		}
	}
	
	
}
