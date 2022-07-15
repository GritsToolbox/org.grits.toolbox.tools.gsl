package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.LipidAnnotation;

public class GlycolipidAnnotationReport extends AnnotationReport {

	private List<AnnotatedStructure> m_lCompositions;
	private Map<AnnotatedStructure, List<Annotation>> m_mapCompositionToAnnotations;
	private Map<String, List<GlycanAnnotation>> m_mapGlycanCompositionToAnnotations;
	private Map<String, List<LipidAnnotation>> m_mapLipidCompositionToAnnotations;
	private Map<Annotation, Double> m_mapAnnotationToCountingScore;
	private Map<Annotation, Double> m_mapAnnotationToIntensityScore;

	public GlycolipidAnnotationReport() {
		super();
		this.m_lCompositions = new ArrayList<>();
		this.m_mapCompositionToAnnotations = new HashMap<>();
		this.m_mapGlycanCompositionToAnnotations = new HashMap<>();
		this.m_mapLipidCompositionToAnnotations = new HashMap<>();
		this.m_mapAnnotationToCountingScore = new HashMap<>();
		this.m_mapAnnotationToIntensityScore = new HashMap<>();
	}

	public void addAnnotationToComposition(AnnotatedStructure a_asComp, Annotation a_glAnnot) {
		if ( !this.m_lCompositions.contains(a_asComp) ) {
			this.m_lCompositions.add(a_asComp);
			this.m_mapCompositionToAnnotations.put(a_asComp, new ArrayList<>());
		}
		// No duplicate
		if ( this.m_mapCompositionToAnnotations.get(a_asComp).contains(a_glAnnot) )
			return;
		this.m_mapCompositionToAnnotations.get(a_asComp).add(a_glAnnot);
	}

	public List<AnnotatedStructure> getGlycolipidCompositions() {
		return this.m_lCompositions;
	}

	public List<Annotation> getAnnotations(AnnotatedStructure a_asGLComp) {
		if ( this.m_mapCompositionToAnnotations.containsKey(a_asGLComp) )
			return this.m_mapCompositionToAnnotations.get(a_asGLComp);
		return null;
	}

	public void addGlycanAnnotationToCompositionGroup(String a_strGComp, GlycanAnnotation a_gAnnot) {
		if ( !this.m_mapGlycanCompositionToAnnotations.containsKey(a_strGComp) )
			this.m_mapGlycanCompositionToAnnotations.put(a_strGComp, new ArrayList<>());
		// No duplicate
		if ( this.m_mapGlycanCompositionToAnnotations.get(a_strGComp).contains(a_gAnnot) )
			return;
		this.m_mapGlycanCompositionToAnnotations.get(a_strGComp).add(a_gAnnot);
	}

	public List<String> getGlycanCompositions() {
		List<String> t_lGComps = new ArrayList<>();
		t_lGComps.addAll( this.m_mapGlycanCompositionToAnnotations.keySet() );
		Collections.sort(t_lGComps);
		return t_lGComps;
	}

	public List<GlycanAnnotation> getGlycanAnnotations(String a_strGComp) {
		if ( this.m_mapGlycanCompositionToAnnotations.containsKey(a_strGComp) )
			return this.m_mapGlycanCompositionToAnnotations.get(a_strGComp);
		return null;
	}

	public void addLipidAnnotationToCompositionGroup(String a_strLComp, LipidAnnotation a_lAnnot) {
		if ( !this.m_mapLipidCompositionToAnnotations.containsKey(a_strLComp) )
			this.m_mapLipidCompositionToAnnotations.put(a_strLComp, new ArrayList<>());
		// No duplicate
		if ( this.m_mapLipidCompositionToAnnotations.get(a_strLComp).contains(a_lAnnot) )
			return;
		this.m_mapLipidCompositionToAnnotations.get(a_strLComp).add(a_lAnnot);
	}

	public List<String> getLipidCompositions() {
		List<String> t_lLComps = new ArrayList<>();
		t_lLComps.addAll( this.m_mapLipidCompositionToAnnotations.keySet() );
		Collections.sort(t_lLComps);
		return t_lLComps;
	}

	public List<LipidAnnotation> getLipidAnnotations(String a_strLComp) {
		if ( this.m_mapLipidCompositionToAnnotations.containsKey(a_strLComp) )
			return this.m_mapLipidCompositionToAnnotations.get(a_strLComp);
		return null;
	}

	public void putAnnotationToScore(Annotation a_annot, Double a_dCntScore, Double a_dIntScore) {
		this.m_mapAnnotationToCountingScore.put(a_annot, a_dCntScore);
		this.m_mapAnnotationToIntensityScore.put(a_annot, a_dIntScore);
	}

	public Double getCountingScore(Annotation a_annot) {
		return this.m_mapAnnotationToCountingScore.get(a_annot);
	}

	public Double getIntensityScore(Annotation a_annot) {
		return this.m_mapAnnotationToIntensityScore.get(a_annot);
	}
}
