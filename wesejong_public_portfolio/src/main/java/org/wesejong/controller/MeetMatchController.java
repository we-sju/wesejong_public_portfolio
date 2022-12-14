package org.wesejong.controller;



import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wesejong.domain.AlarmVO;
import org.wesejong.domain.MeetMatchApplicationDetailsVO;
import org.wesejong.domain.MeetMatchApplicationHistoryVO;
import org.wesejong.domain.MeetMatchManageVO;
import org.wesejong.domain.MeetMatchPersonnelManageVO;
import org.wesejong.domain.MeetMatchProfileVO;
import org.wesejong.domain.MeetMatchTeamMateVO;
import org.wesejong.domain.MeetMatchTeamVO;
import org.wesejong.domain.MemberVO;
import org.wesejong.security.domain.CustomUser;
import org.wesejong.service.AlarmService;
import org.wesejong.service.MeetMatchDepartmentService;
import org.wesejong.service.MeetMatchManageService;
import org.wesejong.service.MeetMatchProfileService;
import org.wesejong.service.MeetMatchTeamMateService;
import org.wesejong.service.MeetMatchTeamService;
import org.wesejong.service.MemberService;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/meetmatch/event/*")
@Log4j
public class MeetMatchController {
	
	@Setter(onMethod_=@Autowired)
	private MemberService memberservice;
	
	@Setter(onMethod_=@Autowired)
	private MeetMatchManageService meetmatchmanageservice;

	@Setter(onMethod_=@Autowired)
	private MeetMatchProfileService meetmatchprofileservice;
	
	@Setter(onMethod_=@Autowired)
	private MeetMatchTeamService meetmatchteamservice;
	
	@Setter(onMethod_=@Autowired)
	private MeetMatchTeamMateService meetmatchteammateservice;
	
	@Setter(onMethod_=@Autowired)
	private MeetMatchDepartmentService meetmatchdepartmentservice;

	@Setter(onMethod_=@Autowired)
	private AlarmService alarmservice;
	
	@GetMapping("/explanation")
	public void dolistPage(Model model) {
		log.info("gotoexplanationpage....");
		
//		??????????????? ???????????????, ?????? ????????? ????????? meetmatchmanage??? ????????? ???????????????.
		
		MeetMatchManageVO meetmatchmanagevo = meetmatchmanageservice.get_MeetMatchManageVO_by_read_Max_meetmatchmanage_seq();
		model.addAttribute("meetmatchmanage",meetmatchmanagevo);
		model.addAttribute("meetmatchlist", meetmatchmanageservice.getListWith_meetmatchpersonnelmanage_by_meetmatchmanage_seq(meetmatchmanagevo));
	}
	
	
	@GetMapping("/apply")
	public void doapplyPage(Model model, Authentication authentication) {
		
		MeetMatchProfileVO meetmatchprofilevo = new MeetMatchProfileVO();
		MeetMatchManageVO meetmatchmanagevo = meetmatchmanageservice.get_MeetMatchManageVO_by_read_Max_meetmatchmanage_seq();
		
		if(authentication != null) {
			CustomUser customuser = (CustomUser) authentication.getPrincipal();
			MemberVO membervo = (MemberVO) customuser.getMember();
			meetmatchprofilevo  = meetmatchprofileservice.get_by_mem_seq(membervo.getMem_seq());	
			model.addAttribute("meetmatchprofile", meetmatchprofilevo);
			

		}
			model.addAttribute("meetmatchmanage",meetmatchmanagevo);
			model.addAttribute("meetmatchlist", meetmatchmanageservice.getListWith_meetmatchpersonnelmanage_by_meetmatchmanage_seq(meetmatchmanagevo));		
	}

//	postmapping????????? request.getParameter??? ????????? ??? ????????????.		
	@PostMapping("/apply")
	public String apply(MeetMatchPersonnelManageVO meetmatchpersonnelvo, MeetMatchTeamVO meetmatchteamvo, MeetMatchTeamMateVO meetmatchteammatevo, Model model, HttpServletRequest request, Authentication authentiation) {
		log.info("gotoapplyregister");
		MeetMatchManageVO meetmatchmanagevo = meetmatchmanageservice.get_MeetMatchManageVO_by_read_Max_meetmatchmanage_seq();
		Long meetmatchmanage_seq = meetmatchmanagevo.getMeetmatchmanage_seq();
		
//		System.out.println(meetmatchmanagevo);
//		System.out.println(meetmatchteamvo);
//		System.out.println(meetmatchteammatevo);
//		System.out.println(meetmatchpersonnelvo);
		
		MemberVO membervo = new MemberVO();
		String[] meetmatchteammate_mem_userid = request.getParameterValues("meetmatchteammate_mem_userid");
		
		
//		?????????????????? ?????? ????????????????????????.
		for(int i=0;i<meetmatchteammate_mem_userid.length;i++) {
			for(int j=0;j<meetmatchteammate_mem_userid.length;j++) {
				if(i != j) {
					if(meetmatchteammate_mem_userid[i].equals(meetmatchteammate_mem_userid[j])) {
						System.out.println("same mem_userid");
						return "/meetmatch/event/applyfail";
					}
				}
			}
		}
		
//		?????? ???????????? ??????????????? ?????????test?????????.
//		????????? ????????? ???????????? ????????? ???????????? ????????????????????????.
		for(int i=0;i<meetmatchteammate_mem_userid.length;i++) {
			membervo.setMem_userid(meetmatchteammate_mem_userid[i]);
			if(memberservice.get_by_mem_userid(membervo) == null) {
				System.out.println("not existing membervo");
				return "/meetmatch/event/applyfail";
			}
		}
		
//		???????????? ????????? ????????? ????????? ???????????????.
//		?????? ???????????? ????????? meetmatchteammate_list??? ?????? ???????????????.
//		meetmatchteammate_list??? meetmatchteam_seq??? ????????????,  
//		????????? meetmatchmanagevo??? meetmatchmanage_seq??? ?????? ?????? ??????????????? 
//		return "/meetmatch/event/applyfail"; ??? ?????????????????????.
		
		List<MeetMatchTeamMateVO> meetmatchteammatevo_list = new ArrayList<MeetMatchTeamMateVO>();
		List<MeetMatchTeamVO> meetmatchteamvo_list = new ArrayList<MeetMatchTeamVO>();
		
		for(int i=0;i<meetmatchteammate_mem_userid.length;i++) {
			membervo.setMem_userid(meetmatchteammate_mem_userid[i]);
			
			membervo = memberservice.get_by_mem_userid(membervo);
			meetmatchteammatevo_list = meetmatchteammateservice.getList_by_mem_seq_of_member(membervo);
			meetmatchteamvo_list = meetmatchteamservice.getList_by_meetmatchteam_seq_of_meetmatchteammate(meetmatchteammatevo_list);
			
			for(int j=0;j<meetmatchteamvo_list.size();j++) {
				if(meetmatchteamvo_list.get(j).getMeetmatchmanage_seq() == meetmatchmanage_seq) {
					System.out.println("not multi apply!!!!");
					return "/meetmatch/event/applyfail";	
				}
			}
		}
		
//		meetmatchteam??? ????????? ?????? ????????????.
		meetmatchteamvo.setMeetmatchmanage_seq(meetmatchmanage_seq);
		meetmatchteamvo.setMeetmatchteam_certified((long) 0);
		meetmatchteamvo.setMeetmatchteam_matchedflag((long) 0);
		meetmatchteamvo.setMeetmatchteammate_certifiedcount((long) 1);

		meetmatchteamservice.register(meetmatchteamvo);
		
		Long meetmatchteam_seq;
		if(meetmatchteamvo.getMeetmatchteam_seq() == null) {
			meetmatchteam_seq = (long) 1;
		}else {
			meetmatchteam_seq = meetmatchteamvo.getMeetmatchteam_seq()+1;
		}
		
		meetmatchteamvo.setMeetmatchteam_seq(meetmatchteam_seq);
		
		
		System.out.println("log meetmatchteamvo's meetmatchteam_seq:"+meetmatchteamvo);
		
//		meetmatchteammate??? ???????????? ???????????? ????????????.
		for(int i=0;i<meetmatchteammate_mem_userid.length;i++) {
			membervo.setMem_userid(meetmatchteammate_mem_userid[i]);
			membervo = memberservice.get_by_mem_userid(membervo);
			meetmatchteammatevo.setMeetmatchteammate_gender(meetmatchteamvo.getMeetmatchteam_gender());
			meetmatchteammatevo.setMeetmatchteammate_department("");
			meetmatchteammatevo.setMeetmatchteammate_teamleaderflag((long) 0);
			meetmatchteammatevo.setMem_seq(membervo.getMem_seq());
			meetmatchteammatevo.setMeetmatchteam_seq(meetmatchteamvo.getMeetmatchteam_seq());
			meetmatchteammatevo.setMeetmatchteammate_certified((long) 0);	
			if(i==0) {
				meetmatchteammatevo.setMeetmatchteammate_teamleaderflag((long) 1);	
				meetmatchteammatevo.setMeetmatchteammate_certified((long) 1);	
			}
			
			
			meetmatchteammateservice.register(meetmatchteammatevo);
			meetmatchteamservice.update_certified_count_by_meetmatchteam_seq(meetmatchteamvo);
//			System.out.println(meetmatchteammatevo);		
		}
		
//		meetmatchteammate??? ????????? ??????????????? alarm ???????????? ????????????.
		AlarmVO alarmvo = new AlarmVO();
		for(int i=0;i<meetmatchteammate_mem_userid.length;i++) {
			membervo.setMem_userid(meetmatchteammate_mem_userid[i]);
			membervo = memberservice.get_by_mem_userid(membervo);
			alarmvo.setAlarm_title("??????????????? ??????????????????????????? ???????????? ??????????????????.");
			alarmvo.setAlarm_content("???????????? ????????? ???????????? ???????????????????????? ??????????????????!");
			alarmvo.setAlarm_writer("meetmatchadmin");
			alarmvo.setAlarm_type("3");
			alarmvo.setMem_seq(membervo.getMem_seq());
			alarmservice.register(alarmvo);
		}
		
		
		return "redirect:/meetmatch/event/applyresult";
////	???????????? meetmatchteamvo??? meetmatchteam_seq??? ???????????????.
//		log meetmatchteamvo's meetmatchteam_seq:MeetMatchTeamVO(meetmatchteam_seq=6, meetmatchteam_gender=male, meetmatchteam_certified=null, meetmatchteam_matchedflag=null, meetmatchteam_regdate=null, meetmatchteam_certifieddate=null, meetmatchmanage_seq=1)
		
//		MemberVO meetmatchteammate_membervo = new MemberVO(); 
//		String[] meetmatchteammate_mem_userid = request.getParameterValues("meetmatchteammate_mem_userid");
//		Long meetmatchmanage_seq = meetmatchteamvo.getMeetmatchmanage_seq();
//		for(int i=0;i<meetmatchteammate_mem_userid.length;i++) {
//			meetmatchteammate_membervo.setMem_userid(meetmatchteammate_mem_userid[i]);
//			MemberVO membervo_mem_seq = memberservice.get_by_mem_userid(meetmatchteammate_membervo);
////			?????? ?????? ???????????? mem_userid??? ???????????? ???????????? ??????????????? ???????????????.
////			meetmatchteammatevo.setMem_seq(membervo_mem_seq.getMem_seq());
//			meetmatchteammatevo.setMeetmatchteammate_gender("");
//			meetmatchteammatevo.setMeetmatchteammate_department("");
//			meetmatchteammatevo.setMeetmatchteammate_teamleaderflag((long)0);
//			meetmatchteammatevo.setMem_seq((long)i);
//			meetmatchteammatevo.setMeetmatchteam_seq(meetmatchteamvo.getMeetmatchteam_seq());
//			if(i==0) {
//				meetmatchteammatevo.setMeetmatchteammate_teamleaderflag((long)1);
//			}
//			
//			meetmatchteammateservice.register(meetmatchteammatevo);
//			System.out.println(meetmatchteammatevo);
//		}
//		
	}
	
	@GetMapping("/applyresult")
	public void doapplyresultPage() {
		log.info("do apply page......");
	}
	
	@GetMapping("/applyfail")
	public void doapplyfailPage() {
		log.info("do apply page......");
	}
	
	
	@GetMapping("/applicationhistory")
	public void doapplicationhistoryPage(Model model, Authentication authentication) {
		log.info("do applicationhistory page......");
		
		List<MeetMatchTeamMateVO> meetmatchteammatevo_list = new ArrayList<MeetMatchTeamMateVO>();
		List<MeetMatchTeamVO> meetmatchteamvo_list = new ArrayList<MeetMatchTeamVO>();
		List<MeetMatchManageVO> meetmatchmanagevo_list = new ArrayList<MeetMatchManageVO>();

		List<MeetMatchApplicationHistoryVO> meetmatchapplicationhistoryvo_list = new ArrayList<MeetMatchApplicationHistoryVO>();

		
		if(authentication != null) {
			CustomUser customuser = (CustomUser) authentication.getPrincipal();
			MemberVO membervo = customuser.getMember();
			
			
			
			
//			??? 3????????? ????????? ?????? ?????? ???????????? ?????????????????? ???????????????.
			meetmatchteammatevo_list = meetmatchteammateservice.getList_by_mem_seq_of_member(membervo);
			meetmatchteamvo_list = meetmatchteamservice.getList_by_meetmatchteam_seq_of_meetmatchteammate(meetmatchteammatevo_list);
			meetmatchmanagevo_list = meetmatchmanageservice.getList_by_meetmatchmanage_seq_of_meetmatchteam(membervo);

//			?????? ???????????? ?????????????????? ???????????? ????????? update????????????.
			for(int i=0;i<meetmatchteamvo_list.size();i++) {
				meetmatchteamservice.update_certified_count_by_meetmatchteam_seq(meetmatchteamvo_list.get(i));
			}
			
			
//			?????????????????? ???????????????. ???????????? ???????????? ????????????.
			for(int i=0;i<meetmatchteamvo_list.size();i++) {
				
//				meetmatchmanage_seq??? ????????? ????????????????????? ?????? For??? ?????????????????????.
						MeetMatchTeamVO meetmatchteamvo = meetmatchteamvo_list.get(i);
						MeetMatchManageVO meetmatchmanagevo = meetmatchmanageservice.get(meetmatchteamvo.getMeetmatchmanage_seq());
						
						MeetMatchApplicationHistoryVO meetmatchapplicationhistoryvo = new MeetMatchApplicationHistoryVO();
						meetmatchapplicationhistoryvo.setMeetmatchmanage_seq(meetmatchmanagevo.getMeetmatchmanage_seq());
						meetmatchapplicationhistoryvo.setMeetmatchmanage_eventid(meetmatchmanagevo.getMeetmatchmanage_eventid());
						meetmatchapplicationhistoryvo.setMeetmatchmanage_eventtitle(meetmatchmanagevo.getMeetmatchmanage_eventtitle());
						meetmatchapplicationhistoryvo.setMeetmatchmanage_eventreleasedate(meetmatchmanagevo.getMeetmatchmanage_eventreleasedate());
						meetmatchapplicationhistoryvo.setMeetmatchmanage_eventendflag(meetmatchmanagevo.getMeetmatchmanage_eventendflag());
						
						meetmatchapplicationhistoryvo.setMeetmatchteam_seq(meetmatchteamvo.getMeetmatchteam_seq());
						meetmatchapplicationhistoryvo.setMeetmatchteam_gender(meetmatchteamvo.getMeetmatchteam_gender());
						meetmatchapplicationhistoryvo.setMeetmatchpersonnelmanage_personnel(meetmatchteamvo.getMeetmatchpersonnelmanage_personnel());
						
						meetmatchapplicationhistoryvo.setMeetmatchteammate_certifiedcount(meetmatchteamvo.getMeetmatchteammate_certifiedcount());
						meetmatchapplicationhistoryvo.setMeetmatchteam_matchedflag(meetmatchteamvo.getMeetmatchteam_matchedflag());
						
						
						meetmatchapplicationhistoryvo_list.add(meetmatchapplicationhistoryvo);
			}
			
			model.addAttribute("meetmatchapplicationhistory", meetmatchapplicationhistoryvo_list);
			
//			model.addAttribute("meetmatchteammate", meetmatchteammatevo);
//			model.addAttribute("meetmatchteam", meetmatchteamservice.getList_by_meetmatchteam_seq_of_meetmatchteammate(meetmatchteammatevo));
//			model.addAttribute("meetmatchmanage", meetmatchmanageservice.getList_by_meetmatchmanage_seq_of_meetmatchteam(membervo));
		}
		
	}
	
	
	@GetMapping("/applicationdetails")
	public void doapplicationdetailsPage(MeetMatchTeamVO meetmatchteamvo, Model model, Authentication authentication) {
		log.info("do applicationdetails page......");
		
		List<MeetMatchTeamMateVO> meetmatchteammatevo_list = new ArrayList<MeetMatchTeamMateVO>();
		List<MeetMatchTeamVO> meetmatchteamvo_list = new ArrayList<MeetMatchTeamVO>();
		List<MeetMatchManageVO> meetmatchmanagevo_list = new ArrayList<MeetMatchManageVO>();

		List<MeetMatchApplicationDetailsVO> meetmatchapplicationdetailsvo_list = new ArrayList<MeetMatchApplicationDetailsVO>();
		
		
////???????????? meetmatchteam??? ???????????????.		
		meetmatchteamvo = meetmatchteamservice.get(meetmatchteamvo.getMeetmatchteam_seq());
		model.addAttribute("meetmatchteam", meetmatchteamvo);
		if(authentication != null) {
			CustomUser customuser = (CustomUser) authentication.getPrincipal();
			MemberVO membervo = customuser.getMember();
			
//meetmatchteam??? meetmatchteammate?????? ?????? ???????????????.
			meetmatchteammatevo_list = meetmatchteammateservice.getList_by_meetmatchteam_seq_of_meetmatchteam(meetmatchteamvo, membervo);
			System.out.println(meetmatchteammatevo_list);
			
			
			for(int i=0;i<meetmatchteammatevo_list.size();i++) {
				MeetMatchApplicationDetailsVO meetmatchapplicationdetailsvo = new MeetMatchApplicationDetailsVO();
				MemberVO membervo_for_mem_seq = new MemberVO();
				
				membervo_for_mem_seq.setMem_seq(meetmatchteammatevo_list.get(i).getMem_seq());				
				System.out.println(memberservice.get_by_mem_seq(membervo_for_mem_seq));
				membervo_for_mem_seq = memberservice.get_by_mem_seq(membervo_for_mem_seq);
				
				if(membervo_for_mem_seq == null) {
					meetmatchapplicationdetailsvo.setMem_userid("????????? ??????");
				}else {
					meetmatchapplicationdetailsvo.setMem_userid(membervo_for_mem_seq.getMem_userid());	
				}
				
				
				meetmatchapplicationdetailsvo.setMeetmatchteammate_gender(meetmatchteammatevo_list.get(i).getMeetmatchteammate_gender());
				meetmatchapplicationdetailsvo.setMeetmatchteaammate_regdate(meetmatchteammatevo_list.get(i).getMeetmatchteammate_regdate());
				meetmatchapplicationdetailsvo.setMeetmatchteammate_certified(meetmatchteammatevo_list.get(i).getMeetmatchteammate_certified());
				meetmatchapplicationdetailsvo.setMeetmatchteam_matchedflag(meetmatchteamvo.getMeetmatchteam_matchedflag());
				
				meetmatchapplicationdetailsvo_list.add(meetmatchapplicationdetailsvo);				
			}
			
			model.addAttribute("meetmatchapplicationdetails", meetmatchapplicationdetailsvo_list);
			meetmatchteamservice.update_certified_count_by_meetmatchteam_seq(meetmatchteamvo);
		}
		
		
	}
	
	@PostMapping("/applicationdetails_confirm")
	public String postapplicationdetails_confirm(MeetMatchTeamVO meetmatchteamvo, Authentication authentication) {
		
		if(authentication != null) {
			CustomUser customuser = (CustomUser) authentication.getPrincipal();
			MemberVO membervo = customuser.getMember();
			
			
			
			MeetMatchTeamMateVO meetmatchteammatevo = new MeetMatchTeamMateVO();
			meetmatchteammatevo.setMeetmatchteam_seq(meetmatchteamvo.getMeetmatchteam_seq());
			meetmatchteammatevo.setMem_seq(membervo.getMem_seq());
			
			meetmatchteammatevo = meetmatchteammateservice.get_by_meetmatchteam_seq_and_mem_seq(meetmatchteammatevo);
			
			
			if(meetmatchteammatevo.getMeetmatchteammate_certified() == 1) {
				meetmatchteammatevo.setMeetmatchteammate_certified((long) 0); 				
				meetmatchteammateservice.update_meetmatchteammate_certified_by_meetmatchteam_seq_and_mem_seq(meetmatchteammatevo);
					
				AlarmVO alarmvo = new AlarmVO();
				alarmvo.setAlarm_title("??????????????? ??????????????????????????? ????????????????????? ??????????????????.");
				alarmvo.setAlarm_content("????????? ????????? ????????????????????? ?????? ????????? ?????? ???????????????!");
				alarmvo.setAlarm_writer("meetmatchadmin");
				alarmvo.setAlarm_type("3");
				alarmvo.setMem_seq(membervo.getMem_seq());
				alarmservice.register(alarmvo);
				
			}
				else if(meetmatchteammatevo.getMeetmatchteammate_certified() == 0){
				meetmatchteammatevo.setMeetmatchteammate_certified((long) 1); 
				meetmatchteammateservice.update_meetmatchteammate_certified_by_meetmatchteam_seq_and_mem_seq(meetmatchteammatevo);
				
				AlarmVO alarmvo = new AlarmVO();
				alarmvo.setAlarm_title("??????????????? ??????????????????????????? ????????????????????? ??????????????????.");
				alarmvo.setAlarm_content("????????? ????????? ????????????????????? ?????? ????????? ?????? ???????????????!");
				alarmvo.setAlarm_writer("meetmatchadmin");
				alarmvo.setAlarm_type("3");
				alarmvo.setMem_seq(membervo.getMem_seq());
				alarmservice.register(alarmvo);
				
			}
			
		}
		
		return "redirect:/meetmatch/event/applicationdetails?meetmatchteam_seq="+meetmatchteamvo.getMeetmatchteam_seq();
		
		
	}
	
	
//	@GetMapping("/teammateconfirmdetails")
//	public void doconfirmdetailsPage(Model model, HttpServletRequest request, Authentication authentication) {
//		log.info("do confirm page......");
//		List<MeetMatchTeamMateVO> list_meetmatchteammatevo = new ArrayList<MeetMatchTeamMateVO>();
//		MeetMatchTeamMateVO meetmatchteammatevo = new MeetMatchTeamMateVO();
//		long meetmatchteam_seq = Long.parseLong(request.getParameter("meetmatchteam_seq"));
//		meetmatchteammatevo.setMeetmatchteam_seq(meetmatchteam_seq);		
//		if(authentication!=null) {
//			CustomUser customuser = (CustomUser) authentication.getPrincipal();
//			list_meetmatchteammatevo = meetmatchteammateservice.getList_by_meetmatchteam_seq_of_meetmatchteammate(meetmatchteammatevo, customuser.getMember());
//			model.addAttribute("meetmatchteammate",list_meetmatchteammatevo);
//		}
//		
//	}


//	@GetMapping("/applicationdetailshistory")
//	public void doapplicationdetailshistoryPage(Model model, Authentication authentication) {
////		???????????? applicationdetailshistory.jsp : ????????? ???????????? ???????????? ??????(???????????? ??????)??? ???????????? ???????????????.
////		1.???????????????, meetmatchteammate?????? session??? mem_seq??? ?????? meetmatchteammatevo?????? list????????? ?????? ???????????????.
////		2.meetmatchteammatevo list????????? meetmatchteam_seq??? ?????? meetmatchteamvo list??? ???????????????.
////		3.meetmatchteamvo??? ???????????? ???????????? ???????????????.
//		log.info("do application detailshistory page...");
//		List<MeetMatchTeamMateVO> meetmatchteammatevo = new ArrayList<MeetMatchTeamMateVO>();
//		List<MeetMatchTeamVO> meetmatchteamvo = new ArrayList<MeetMatchTeamVO>();
//		List<MeetMatchManageVO> meetmatchmanagevo = new ArrayList<MeetMatchManageVO>();
//		
//		if(authentication!=null) {
//			CustomUser customuser = (CustomUser) authentication.getPrincipal();
//			meetmatchteammatevo = meetmatchteammateservice.getList_by_mem_seq_of_member(customuser.getMember());
//			
//			model.addAttribute("meetmatchteammate",meetmatchteammatevo);
//			model.addAttribute("meetmatchteam",meetmatchteamservice.getList_by_meetmatchteam_seq_of_meetmatchteammate(meetmatchteammatevo));
//			model.addAttribute("meetmatchmanage", meetmatchmanageservice.getList_by_meetmatchmanage_seq_of_meetmatchteam(customuser.getMember()));	
//		}
//	}
	
//	@GetMapping("/explanation")
//	public void dolistPage(Model model) {
//		log.info("gotoexplanationpage....");
////		model.addAttribute("meetmatchlist", meetmatchservice.getListWith_meetmatchpersonnelmanage());
////		??????????????? ???????????????, ?????? ????????? ????????? meetmatchmanage??? ????????? ???????????????.
//		model.addAttribute("meetmatchmanage",meetmatchmanageservice.get_MeetMatchManageVO_by_read_Max_meetmatchmanage_seq());
//	}
//
//	@GetMapping("/profile")
//	public void doprofilePage(Model model, HttpServletRequest request, Authentication authentication) {
//		log.info("gotoprofilepage");
//		
//		List<MeetMatchDepartmentVO> meetmatchdepartmentvo_list = meetmatchdepartmentservice.getList();
//		MeetMatchManageVO meetmatchmanagevo = new MeetMatchManageVO();
//		MemberVO membervo = new MemberVO();
//		MeetMatchProfileVO meetmatchprofilevo = new MeetMatchProfileVO();
//		
//		Long meetmatchmanage_seq = Long.parseLong(request.getParameter("meetmatchmanage_seq"));
//		meetmatchmanagevo.setMeetmatchmanage_seq(meetmatchmanage_seq);
////		System.out.println(authentication);
//		
////		?????????:spring security CustomUser cannot be cast to
////		https://stackoverflow.com/questions/32465706/spring-securitiy-retrieving-data-from-custom-user
//		
//		if(authentication!=null) {	//?????? ???????????? ????????????, meetmatchprofile ???????????? ???????????? ??????????????? ???????????? ???????????? ???????????????.
//			CustomUser customuser = (CustomUser) authentication.getPrincipal();
//			System.out.println(customuser);
//			meetmatchprofilevo = meetmatchprofileservice.get_by_mem_seq(customuser.getMember().getMem_seq());
//			System.out.println(meetmatchprofilevo);
//			model.addAttribute("meetmatchprofile",meetmatchprofilevo);
//		}
//
//		model.addAttribute("meetmatchmanage", meetmatchmanagevo);
//		model.addAttribute("meetmatchdepartment",meetmatchdepartmentvo_list);
//		
//	}
//	
//	@PostMapping("/profile")
//	public String profile(MeetMatchProfileVO meetmatchprofilevo, MeetMatchManageVO meetmatchmanagevo, Model model, HttpServletRequest request, Authentication authentication) {
//			
//		if(authentication!=null) {	//?????? ???????????? ????????????, 
//			CustomUser customuser = (CustomUser) authentication.getPrincipal();
//			System.out.println(customuser);
//			meetmatchprofilevo.setMem_seq(customuser.getMember().getMem_seq());
//		}
//		
//		if(meetmatchprofileservice.getExists_by_mem_seq(meetmatchprofilevo.getMem_seq())==0) {
//			meetmatchprofileservice.register(meetmatchprofilevo);
//		}
//		
////		Long meetmatchmanage_seq = Long.parseLong(request.getParameter("meetmatchmanage_seq"));
////		hidden?????? ????????? meetmatchmanage_seq??? ????????????.
//		Long meetmatchmanage_seq = meetmatchmanagevo.getMeetmatchmanage_seq();
//		System.out.println(meetmatchmanagevo);
//		
////		return "meetmatch/event/apply?meetmatchmanage_seq="+meetmatchmanage_seq;
////		return "meetmatch/event/apply"; forward ???????????? apply??? ??????????????????, forward???????????? ???????????? Controller?????? forward??? ????????? ????????????????????? ???????????? ????????????.
////		????????? RequestDispatcher rd = request.getRequestDispatcher("b.jsp"); ???????????? ???????????? ??????????????????, ?????? redirect??? ????????????.        
////		hidden?????? ????????? meetmatchmanage_seq??? ????????????.
//		return "redirect:/meetmatch/event/apply?meetmatchmanage_seq="+meetmatchmanage_seq;
//	}
//	
//	@GetMapping("/apply")
//	public void doapplyPage(MeetMatchManageVO meetmatchmanagevo, Model model, HttpServletRequest request, Authentication authentication) {
////		?????? ?????????
//		meetmatchmanagevo = meetmatchmanageservice.get(meetmatchmanagevo.getMeetmatchmanage_seq());	//request parameter meetmatchmanage_seq ????????????.
//		MemberVO membervo = new MemberVO();
//		MeetMatchProfileVO meetmatchprofilevo = new MeetMatchProfileVO();
//		if(authentication!=null) {	//??????????????? ?????? ????????? security?????? ?????? ?????????????????? ??????????????????.
//			CustomUser customuser = (CustomUser) authentication.getPrincipal();
//			if(meetmatchprofileservice.getExists_by_mem_seq(customuser.getMember().getMem_seq()) != 1){
////				?????? meetmatchprofilevo??? ???????????? ????????????, ????????? ?????????????????? ????????? ????????????.?????? ??????????????? ?????????.
////				https://okky.kr/article/578596 ???????????? ?????????????????? ?????????????????????.
//				meetmatchprofilevo = null;
//			}else if(meetmatchprofileservice.getExists_by_mem_seq(customuser.getMember().getMem_seq()) == 1){
//				meetmatchprofilevo = meetmatchprofileservice.get_by_mem_seq(customuser.getMember().getMem_seq());
//			}
//		}
//		
//		
//		log.info("gotoapplypage");
//		System.out.println(meetmatchmanagevo);
//		System.out.println("applypagesysout");
//		
//		
//		model.addAttribute("meetmatchmanage", meetmatchmanagevo);
//		model.addAttribute("meetmatchprofile",meetmatchprofilevo);
//		
//	}
//	
//	@PostMapping("/apply")
//	public String apply(MeetMatchManageVO meetmatchmanagevo, MeetMatchPersonnelManageVO meetmatchpersonnelvo, MeetMatchTeamVO meetmatchteamvo, MeetMatchTeamMateVO meetmatchteammatevo, Model model, HttpServletRequest request, Authentication authentiation) {
//		log.info("gotoapplyregister");
//		
////		postmapping????????? request.getParameter??? ????????? ??? ????????????.
//		
//		meetmatchteamvo.setMeetmatchteam_certified((long)0);
//		
//		meetmatchteamvo.setMeetmatchteam_matchedflag((long)0);
//		meetmatchteamvo.setMeetmatchteammate_certifiedcount((long)0);
////		???????????? meetmatchteamvo??? meetmatchteam_seq??? ???????????????.
//		meetmatchteamservice.register(meetmatchteamvo);
////		System.out.println("log meetmatchteamvo's meetmatchteam_seq:"+meetmatchteamvo); 
////		log meetmatchteamvo's meetmatchteam_seq:MeetMatchTeamVO(meetmatchteam_seq=6, meetmatchteam_gender=male, meetmatchteam_certified=null, meetmatchteam_matchedflag=null, meetmatchteam_regdate=null, meetmatchteam_certifieddate=null, meetmatchmanage_seq=1)
//		MemberVO meetmatchteammate_membervo = new MemberVO(); 
//		String[] meetmatchteammate_mem_userid = request.getParameterValues("meetmatchteammate_mem_userid");
//		Long meetmatchmanage_seq = meetmatchteamvo.getMeetmatchmanage_seq();
//		for(int i=0;i<meetmatchteammate_mem_userid.length;i++) {
//			meetmatchteammate_membervo.setMem_userid(meetmatchteammate_mem_userid[i]);
//			MemberVO membervo_mem_seq = memberservice.get_by_mem_userid(meetmatchteammate_membervo);
////			?????? ?????? ???????????? mem_userid??? ???????????? ???????????? ??????????????? ???????????????.
////			meetmatchteammatevo.setMem_seq(membervo_mem_seq.getMem_seq());
//			meetmatchteammatevo.setMeetmatchteammate_gender("");
//			meetmatchteammatevo.setMeetmatchteammate_department("");
//			meetmatchteammatevo.setMeetmatchteammate_teamleaderflag((long)0);
//			meetmatchteammatevo.setMem_seq((long)i);
//			meetmatchteammatevo.setMeetmatchteam_seq(meetmatchteamvo.getMeetmatchteam_seq());
//			if(i==0) {
//				meetmatchteammatevo.setMeetmatchteammate_teamleaderflag((long)1);
//			}
//			
//			meetmatchteammateservice.register(meetmatchteammatevo);
//			System.out.println(meetmatchteammatevo);
//		}
//		
//		return "redirect:/meetmatch/event/applyresult";
//	}
//	
//	@GetMapping("/applyresult")
//	public void doapplyresultPage() {
//		log.info("do apply page......");
//	}
//	
//	@GetMapping("/applicationdetailshistory")
//	public void doapplicationdetailshistoryPage(Model model, Authentication authentication) {
////		???????????? applicationdetailshistory.jsp : ????????? ???????????? ???????????? ??????(???????????? ??????)??? ???????????? ???????????????.
////		1.???????????????, meetmatchteammate?????? session??? mem_seq??? ?????? meetmatchteammatevo?????? list????????? ?????? ???????????????.
////		2.meetmatchteammatevo list????????? meetmatchteam_seq??? ?????? meetmatchteamvo list??? ???????????????.
////		3.meetmatchteamvo??? ???????????? ???????????? ???????????????.
//		log.info("do application detailshistory page...");
//		List<MeetMatchTeamMateVO> meetmatchteammatevo = new ArrayList<MeetMatchTeamMateVO>();
//		List<MeetMatchTeamVO> meetmatchteamvo = new ArrayList<MeetMatchTeamVO>();
//		List<MeetMatchManageVO> meetmatchmanagevo = new ArrayList<MeetMatchManageVO>();
//		
//		if(authentication!=null) {
//			CustomUser customuser = (CustomUser) authentication.getPrincipal();
//			meetmatchteammatevo = meetmatchteammateservice.getList_by_mem_seq_of_member(customuser.getMember());
//			
//			model.addAttribute("meetmatchteammate",meetmatchteammatevo);
//			model.addAttribute("meetmatchteam",meetmatchteamservice.getList_by_meetmatchteam_seq_of_meetmatchteammate(meetmatchteammatevo));
//			model.addAttribute("meetmatchmanage", meetmatchmanageservice.getList_by_meetmatchmanage_seq_of_meetmatchteam(customuser.getMember()));	
//		}
//	}
//	
//	@GetMapping("/teammateconfirmdetails")
//	public void doconfirmdetailsPage(Model model, HttpServletRequest request, Authentication authentication) {
//		log.info("do confirm page......");
//		List<MeetMatchTeamMateVO> list_meetmatchteammatevo = new ArrayList<MeetMatchTeamMateVO>();
//		MeetMatchTeamMateVO meetmatchteammatevo = new MeetMatchTeamMateVO();
//		long meetmatchteam_seq = Long.parseLong(request.getParameter("meetmatchteam_seq"));
//		meetmatchteammatevo.setMeetmatchteam_seq(meetmatchteam_seq);		
//		if(authentication!=null) {
//			CustomUser customuser = (CustomUser) authentication.getPrincipal();
//			list_meetmatchteammatevo = meetmatchteammateservice.getList_by_meetmatchteam_seq_of_meetmatchteammate(meetmatchteammatevo, customuser.getMember());
//			model.addAttribute("meetmatchteammate",list_meetmatchteammatevo);
//		}
//		
//	}
	
	
}
