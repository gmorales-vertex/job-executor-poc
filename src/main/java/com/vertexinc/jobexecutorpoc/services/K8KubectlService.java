package com.vertexinc.jobexecutorpoc.services;

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.ClientBuilder;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class K8KubectlService {

  private final String NAMESPACE = "default";
  private final String APP_NAME = "edge";
  private final String JOB_DEPLOYMENT_NAME = "edge-deployment";

  public K8KubectlService() throws IOException {
    Configuration.setDefaultApiClient(ClientBuilder.defaultClient());
  }

  public void listPods() {
    try {
      List<V1Pod> pods = Kubectl.get(V1Pod.class).namespace("default").execute();
      pods.forEach(
          pod -> {
            System.out.println(
                String.format(
                    "Pod Name: %s   Status: %s",
                    pod.getMetadata().getName(), pod.getStatus().getPhase()));
          });
    } catch (KubectlException e) {
      if (e.getCause() instanceof ApiException) {
        System.err.println(((ApiException) e.getCause()).getResponseBody());
      } else {
        System.err.println(e.getMessage());
      }
    }
  }

  public void scale(int numberOfInstance) {
    try {
      Kubectl.patch(V1Deployment.class)
          .patchType(V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH)
          .namespace(NAMESPACE)
          .name(JOB_DEPLOYMENT_NAME)
          .patchContent(
              new V1Patch(String.format("{\"spec\":{\"replicas\":%s}}", numberOfInstance)))
          .execute();
      System.out.println(
          String.format(
              "Successfully scaled %s App to %d number of instance", APP_NAME, numberOfInstance));
    } catch (KubectlException e) {
      e.printStackTrace();
      if (e.getCause() instanceof ApiException) {
        System.out.println(((ApiException) e.getCause()).getResponseBody());
      } else {
        System.err.println(e.getMessage());
      }
    }
  }

  public void updateImage(String imageName) {
    try {
      Kubectl.patch(V1Deployment.class)
          .patchType(V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH)
          .namespace(NAMESPACE)
          .name(JOB_DEPLOYMENT_NAME)
          .patchContent(
              new V1Patch(
                  String.format(
                      "{\"spec\":{\"template\":{\"spec\":{\"containers\": [{\"name\": \"edge\", \"image\": \"%s\"}]}}}}",
                      imageName)))
          .execute();
      System.out.println(
              String.format(
                      "Successfully changed %s app image to %s", APP_NAME, imageName));
    } catch (KubectlException e) {
      e.printStackTrace();
      if (e.getCause() instanceof ApiException) {
        System.out.println(((ApiException) e.getCause()).getResponseBody());
      } else {
        System.err.println(e.getMessage());
      }
    }
  }
}
