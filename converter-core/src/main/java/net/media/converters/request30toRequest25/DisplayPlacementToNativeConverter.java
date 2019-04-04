package net.media.converters.request30toRequest25;

import com.fasterxml.jackson.core.JsonProcessingException;

import net.media.driver.Conversion;
import net.media.exceptions.OpenRtbConverterException;
import net.media.config.Config;
import net.media.converters.Converter;
import net.media.openrtb25.request.Native;
import net.media.openrtb25.request.NativeRequest;
import net.media.openrtb25.request.NativeRequestBody;
import net.media.openrtb3.DisplayPlacement;
import net.media.openrtb3.NativeFormat;
import net.media.utils.JacksonObjectMapper;
import net.media.utils.Provider;
import net.media.utils.Utils;

import java.util.HashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DisplayPlacementToNativeConverter implements Converter<DisplayPlacement, Native> {

  @Override
  public Native map(DisplayPlacement displayPlacement, Config config, Provider converterProvider) throws OpenRtbConverterException {
    if (displayPlacement == null) {
      return null;
    }
    Native nat = new Native();
    enhance(displayPlacement, nat, config, converterProvider);
    if (isNull(nat.getNativeRequestBody())) {
      return null;
    }
    return nat;
  }

  @Override
  public void enhance(DisplayPlacement displayPlacement, Native nat, Config config, Provider converterProvider) throws OpenRtbConverterException {
    if (isNull(displayPlacement) || isNull(nat)) {
      return;
    }
    Converter<NativeFormat, NativeRequestBody> nativeFormatNativeRequestBodyConverter =
      converterProvider.fetch(new Conversion(NativeFormat.class, NativeRequestBody.class));
    NativeRequestBody nativeRequestBody = nativeFormatNativeRequestBodyConverter.map
      (displayPlacement.getNativefmt(), config, converterProvider);
    if (isNull(nativeRequestBody)) {
      return;
    }
    NativeRequest nativeRequest = new NativeRequest();
    nativeRequest.setNativeRequestBody(nativeRequestBody);
    if (nonNull(nativeRequest.getNativeRequestBody())) {
      nativeRequest.getNativeRequestBody().setContext(displayPlacement.getContext());
      nativeRequest.getNativeRequestBody().setPlcmttype(displayPlacement.getPtype());
      if (nonNull(displayPlacement.getExt())) {
        nativeRequest.getNativeRequestBody().setContextsubtype((Integer) displayPlacement.getExt
          ().get("contextsubtype"));
      }
    }
    nat.setApi(Utils.copyCollection(displayPlacement.getApi(), config));
    if (nonNull(displayPlacement.getExt())) {
      if (isNull(nat.getExt())) {
        nat.setExt(new HashMap<>());
      }
      nat.getExt().putAll(displayPlacement.getExt());
      nat.setVer((String) displayPlacement.getExt().get("nativeversion"));
      nat.getExt().remove("nativeversion");
    }
    if (config.getNativeRequestAsString()) {
      try {
        nat.setRequest(JacksonObjectMapper.getMapper().writeValueAsString(nativeRequest));
      } catch (JsonProcessingException e) {
        throw new OpenRtbConverterException(e);
      }
    } else {
      nat.setRequest(nativeRequest);
    }
    nat.setExt(Utils.copyMap(displayPlacement.getExt(), config));
  }
}
