/*
 * Copyright  2019 - present. IAB Tech Lab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.media.converters.request30toRequest25;

import net.media.config.Config;
import net.media.converters.Converter;
import net.media.driver.Conversion;
import net.media.exceptions.OpenRtbConverterException;
import net.media.openrtb3.Data;
import net.media.openrtb3.Geo;
import net.media.openrtb3.User;
import net.media.utils.CollectionUtils;
import net.media.utils.CommonConstants;
import net.media.utils.Provider;

import java.util.HashMap;
import java.util.Map;

import static net.media.utils.ExtUtils.putToExt;

public class UserToUserConverter implements Converter<User, net.media.openrtb25.request.User> {

  @Override
  public net.media.openrtb25.request.User map(
      User source, Config config, Provider converterProvider) throws OpenRtbConverterException {
    if (source == null) {
      return null;
    }

    net.media.openrtb25.request.User user1 = new net.media.openrtb25.request.User();

    enhance(source, user1, config, converterProvider);

    return user1;
  }

  @Override
  public void enhance(
      User source,
      net.media.openrtb25.request.User target,
      Config config,
      Provider converterProvider)
      throws OpenRtbConverterException {
    if (source == null || target == null) {
      return;
    }
    Converter<Geo, net.media.openrtb25.request.Geo> geoGeoConverter =
        converterProvider.fetch(new Conversion<>(Geo.class, net.media.openrtb25.request.Geo.class));
    Converter<Data, net.media.openrtb25.request.Data> dataDataConverter =
        converterProvider.fetch(
            new Conversion<>(Data.class, net.media.openrtb25.request.Data.class));
    target.setId(source.getId());
    target.setBuyeruid(source.getBuyeruid());
    target.setYob(source.getYob());
    target.setGender(source.getGender());
    target.setGeo(geoGeoConverter.map(source.getGeo(), config, converterProvider));
    target.setKeywords(source.getKeywords());
    target.setData(
        CollectionUtils.convert(
            source.getData(), dataDataConverter, config, converterProvider));
    Map<String, Object> map = source.getExt();
    if (map != null) {
      target.setExt(new HashMap<>(map));
    }
    putToExt(source::getConsent, target.getExt(), CommonConstants.CONSENT, target::setExt);
  }
}
