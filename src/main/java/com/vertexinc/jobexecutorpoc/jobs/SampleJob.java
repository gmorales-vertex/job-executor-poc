package com.vertexinc.jobexecutorpoc.jobs;

import com.vertexinc.jobexecutorpoc.services.EdgeLinkService;
import com.vertexinc.jobexecutorpoc.services.K8ClientService;
import com.vertexinc.jobexecutorpoc.services.K8KubectlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SampleJob {

  private final K8ClientService k8ClientService;
  private final K8KubectlService k8KubectlService;
  private final EdgeLinkService edgeLinkService;

  @Scheduled(cron = "0/5 * * * * ?")
  public void printPods() {
    System.out.println("Listing available pods ...");
    k8KubectlService.listPods();
  }

  @Scheduled(cron = "0/20 * * * * ?")
  public void processRequest() {
    String clusterId = "1";
    edgeLinkService
        .getNextRequest(clusterId)
        .log()
        .subscribe(
            request -> {
              if (Objects.equals(request.requestType, "scale")) {
                System.out.println(
                    String.format("Scaling pods to %d instance ...", request.numberOfInstance));
                k8KubectlService.scale(request.numberOfInstance);
              } else if (Objects.equals(request.requestType, "updateImage")) {
                System.out.println(
                    String.format("Changing app image to %s ...", request.getImage()));
                k8KubectlService.updateImage(request.getImage());
              } else {
                System.err.println(
                    String.format("Request type: %s is not supported", request.requestType));
              }
            });
  }
}
