package com.justdoit.watchdog.controller;
import com.justdoit.watchdog.model.ContainerLog;
import com.justdoit.watchdog.model.Containers;
import com.justdoit.watchdog.model.DashboardDTO;
import com.justdoit.watchdog.repository.ContainerLogRepository;
import com.justdoit.watchdog.repository.ContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private ContainerLogRepository logRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model){

        List<Containers> allContainers = containerRepository.findAll();
        List<DashboardDTO> dashboardDTOs = new ArrayList<>();

        for (Containers containers: allContainers){
            DashboardDTO dashboardDTO = new DashboardDTO();
            dashboardDTO.setName(containers.getName());
            dashboardDTO.setImage(containers.getImage());

            ContainerLog latestLog = logRepository.findFirstByContainerOrderByLoggedAtDesc(containers);

            if (latestLog != null) {
                dashboardDTO.setStatus(latestLog.getStatus());
                dashboardDTO.setCpu_percent(latestLog.getCpuPercent());
                dashboardDTO.setMemory_usage_mb(latestLog.getMemoryUsageMb());
            } else {
                dashboardDTO.setStatus("Unknown");
                dashboardDTO.setCpu_percent(0.0);
                dashboardDTO.setMemory_usage_mb(0.0);
            }

            dashboardDTOs.add(dashboardDTO);
        }

        model.addAttribute("containerListe", dashboardDTOs);

        return "index";
    }

    @GetMapping("/logs/{name}")
    public String showContainerLogs(@PathVariable String name, Model model){
        Containers container = containerRepository.findByName(name);

        if (container != null) {
            List<ContainerLog> historyLogs = logRepository.findTop120ByContainerOrderByLoggedAtDesc(container);

            model.addAttribute("containerName", container.getName());
            model.addAttribute("logListe", historyLogs);
        }
        return "logs";
        }
    }

