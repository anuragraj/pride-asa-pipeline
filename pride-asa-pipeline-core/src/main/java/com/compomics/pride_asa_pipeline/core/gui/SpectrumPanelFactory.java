/* 
 * Copyright 2018 compomics.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.compomics.pride_asa_pipeline.core.gui;

import com.compomics.pride_asa_pipeline.core.service.SpectrumService;
import com.compomics.pride_asa_pipeline.core.util.PeakUtils;
import com.compomics.pride_asa_pipeline.model.FragmentIonAnnotation;
import com.compomics.pride_asa_pipeline.model.Identification;
import com.compomics.pride_asa_pipeline.model.Peak;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.ReferenceArea;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 *
 * @author Niels Hulstaert
 */
public class SpectrumPanelFactory {

    private SpectrumService spectrumService;

    public SpectrumService getspectrumService() {
        return spectrumService;
    }

    public void setSpectrumService(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    /**
     * Constructs a SpectrumPanel for the given Identification
     *
     * @param identification the identification
     * @param isIdentificationsFile is the source an identifications file or the
     * pride public db instance
     * @return the SpectrumPanel
     */
    public SpectrumPanel getSpectrumPanel(Identification identification, boolean isIdentificationsFile) {

        //get spectrum peaks for the selected identification
        List<Peak> peaks;
        if (isIdentificationsFile) {
            peaks = spectrumService.getSpectrumPeaksBySpectrumId(identification.getSpectrumId());
        } else {
            try {
                peaks = spectrumService.getSpectrumPeaksBySpectrumId(identification.getSpectrumId());
            } catch (EmptyResultDataAccessException ex) {
                peaks = new ArrayList<>();
            }
        }

        SpectrumPanel spectrumPanel = null;
        if (!peaks.isEmpty()) {
            //initialize new SpectrumPanel
            spectrumPanel = new SpectrumPanel(PeakUtils.getMzRatiosAsArray(peaks),
                    PeakUtils.getIntensitiesAsArray(peaks),
                    identification.getPeptide().getMzRatio(),
                    Integer.toString(identification.getPeptide().getCharge()), "", 40, false, false, false);

            // remove the border
            spectrumPanel.setBorder(null);

            spectrumPanel.showAnnotatedPeaksOnly(true);

            //add peak annotations
            if (identification.getAnnotationData().getFragmentIonAnnotations() != null) {
                spectrumPanel.setAnnotations(getPeakAnnotations(identification));
            }

            //add noise threshold area
            ReferenceArea referenceArea = getReferenceArea(identification.getAnnotationData().getNoiseThreshold());
            spectrumPanel.addReferenceAreaYAxis(referenceArea);
        }

        return spectrumPanel;
    }

    /**
     * Constructs the vector with the peak annotations for the spectrum panel.
     *
     * @param identification the identification
     * @return the vector of peak annotations
     */
    private List<SpectrumAnnotation> getPeakAnnotations(Identification identification) {
        List<SpectrumAnnotation> peakAnnotations = new ArrayList();

        for (FragmentIonAnnotation fragmentIonAnnotation : identification.getAnnotationData().getFragmentIonAnnotations()) {
            String label = fragmentIonAnnotation.getIon_type_name().substring(0, 1);
            label += fragmentIonAnnotation.getFragment_ion_number();

            if (fragmentIonAnnotation.getIon_charge() > 1) {
                label += getIonChargeString(fragmentIonAnnotation.getIon_charge());
            }

            DefaultSpectrumAnnotation defaultSpectrumAnnotation = new DefaultSpectrumAnnotation(
                    fragmentIonAnnotation.getMz(), fragmentIonAnnotation.getMass_error(), SpectrumPanel.determineColorOfPeak(label), label);
            peakAnnotations.add(defaultSpectrumAnnotation);
        }

        return peakAnnotations;
    }

    private ReferenceArea getReferenceArea(double noiseThreshold) {
        return new ReferenceArea(
                "", // reference area unique identifier
                "", // reference area label
                0.0, // start of area
                noiseThreshold, // end of area
                Color.blue, // color of area
                0.1f, // transparency level
                true, // drawn on top of or behind the data
                false, 
                false);
    }

    private String getIonChargeString(int ionCharge) {
        StringBuilder ionChargeStringBuilder = new StringBuilder();
        for (int i = 0; i < ionCharge; i++) {
            ionChargeStringBuilder.append("+");
        }

        return ionChargeStringBuilder.toString();
    }
}
