package com.vertexinc.jobexecutorpoc.services;

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.PatchUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class K8ClientService {

  public void listPods() {
    ApiClient client = null;
    try {
      client = ClientBuilder.cluster().build();
      Configuration.setDefaultApiClient(client);
      CoreV1Api api = new CoreV1Api();
      V1PodList list =
          api.listNamespacedPod(
              "default", null, null, null, null, null, null, null, null, null, null);
      for (V1Pod item : list.getItems()) {
        System.out.println(item.getMetadata().getName());
      }
    } catch (IOException | ApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void scale(int numberOfInstance) {
    String jsonPatchStr = String.format("[{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\":%d}]", numberOfInstance);
    try {
      AppsV1Api api = new AppsV1Api(ClientBuilder.standard().build());

      // json-patch a deployment
      V1Deployment deploy2 =
          PatchUtils.patch(
              V1Deployment.class,
              () ->
                  api.patchNamespacedDeploymentCall(
                      "edge-deployment",
                      "default",
                      new V1Patch(jsonPatchStr),
                      null,
                      null,
                      null,
                      null, // field-manager is optional
                      null,
                      null),
              V1Patch.PATCH_FORMAT_JSON_PATCH,
              api.getApiClient());
      System.out.println("json-patched deployment" + deploy2);
    } catch (ApiException e) {
      System.out.println(e.getResponseBody());
      e.printStackTrace();
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }
}
