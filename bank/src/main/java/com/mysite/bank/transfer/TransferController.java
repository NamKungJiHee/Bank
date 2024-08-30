package com.mysite.bank.transfer;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class TransferController {
	private final TransferService transferService;
	
	@GetMapping("/transfer")
	public String selectTransferAccount(Model model, Principal principal) {
		String userName = principal.getName();
	
		List<Map<String, Object>> Info = transferService.groupAccountInfo(userName);
		model.addAttribute("Infos", Info);
		
		return "selectTransfer_form";
	}
	
	@GetMapping("/transferMoney")
	public String transfer(Model model, Principal principal, @RequestParam("accountId") Long accountId) {
		String userName = principal.getName();
		List<Map<String, Object>> groupDetails = transferService.groupAccountList(userName);

	    model.addAttribute("accountId", accountId);
	    model.addAttribute("Infos", groupDetails);
		
		return "transfer_form";
	}
	
	@PostMapping("/transferDirect")
	public String transferDirect(Model model, Principal principal, @RequestParam("accountId") Long accountId, @RequestParam("depositAccountNum") String depositAccountNum, @RequestParam("depositBalance") String depositBalance) {
		String userName = principal.getName();
		
		transferService.saveTransferInfo(userName, accountId, depositAccountNum, depositBalance);
		return "transfer_form";
	}
	
}
