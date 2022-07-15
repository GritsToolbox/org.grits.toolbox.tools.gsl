package org.grits.toolbox.tools.gsl.util.analyze.glycan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.Modification;
import org.eurocarbdb.MolecularFramework.sugar.ModificationType;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.SubstituentType;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserTreeSingle;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

public class GlycoVisitorComposition implements GlycoVisitor
{
    private HashMap<String, Integer> m_composition = new HashMap<String, Integer>();
    public HashMap<String, Integer> getComposition()
    {
        return this.m_composition;
    }

    public void setComposition(HashMap<String, Integer> a_composition)
    {
        this.m_composition = a_composition;
    }

    private HashMap<GlycoNode, Boolean> m_handledResidues = new HashMap<GlycoNode, Boolean>();

    public void clear()
    {
        this.m_composition.clear();
        this.m_handledResidues.clear();
    }

    public GlycoTraverser getTraverser(GlycoVisitor a_visitor) throws GlycoVisitorException
    {
        return new GlycoTraverserTreeSingle(a_visitor);
    }

    public void start(Sugar a_sugar) throws GlycoVisitorException
    {
        GlycoTraverser t_traverser = this.getTraverser(this);
        t_traverser.traverseGraph(a_sugar);
        for (UnderdeterminedSubTree t_tree : a_sugar.getUndeterminedSubTrees())
        {
            t_traverser.traverseGraph(t_tree);
        }
    }

    public void visit(NonMonosaccharide a_arg0) throws GlycoVisitorException
    {
        throw new GlycoVisitorException("NonMonosaccharide are not supported.");
    }

    public void visit(SugarUnitRepeat a_arg0) throws GlycoVisitorException
    {
        throw new GlycoVisitorException("SugarUnitRepeat are not supported.");
    }

    public void visit(SugarUnitCyclic a_arg0) throws GlycoVisitorException
    {
        throw new GlycoVisitorException("SugarUnitCyclic are not supported.");
    }

    public void visit(SugarUnitAlternative a_arg0) throws GlycoVisitorException
    {
        throw new GlycoVisitorException("SugarUnitAlternative are not supported.");
    }

    public void visit(UnvalidatedGlycoNode a_arg0) throws GlycoVisitorException
    {
        throw new GlycoVisitorException("UnvalidatedGlycoNode are not supported.");
    }

    public void visit(GlycoEdge a_arg0) throws GlycoVisitorException
    {
        // nothing to do
    }

    public void visit(Monosaccharide a_ms) throws GlycoVisitorException
    {
        if ( this.m_handledResidues.get(a_ms) == null )
        {
            boolean t_alditol = false;
            String t_ulop = "";
            String t_name = a_ms.getSuperclass().getName();
            for (Modification t_modi : a_ms.getModification())
            {
                if ( t_modi.getModificationType().equals(ModificationType.ACID) )
                {
                    t_name += "A";
                }
                else if ( t_modi.getModificationType().equals(ModificationType.ALDI) )
                {
                    t_alditol = true;
                }
                else if ( t_modi.getModificationType().equals(ModificationType.DEOXY) )
                {
                    t_name = "d" + t_name;
                }
                else if ( t_modi.getModificationType().equals(ModificationType.KETO) )
                {
                    t_ulop += "-ulop";
                }
                else
                {
                    throw new GlycoVisitorException("Unsupported modification type:" + t_modi.getModificationType());
                }
            }
            t_name += t_ulop;
            if ( t_alditol )
            {
                t_name += "-ol";
            }
            HashMap<SubstituentType, Integer> t_substMap = new HashMap<SubstituentType, Integer>();
            List<SubstituentType> t_substList = new ArrayList<SubstituentType>();
            for (GlycoNode t_node : a_ms.getChildNodes())
            {
                GlycoVisitorNodeType t_nodeType = new GlycoVisitorNodeType();
                Substituent t_subst = t_nodeType.getSubstituent(t_node);
                if ( t_subst != null )
                {
                    if ( t_subst.getChildEdges().size() == 0 )
                    {
                        Integer t_int = t_substMap.get(t_subst.getSubstituentType());
                        if ( t_int == null )
                        {
                            t_substMap.put(t_subst.getSubstituentType(), 1);
                            t_substList.add(t_subst.getSubstituentType());
                        }
                        else
                        {
                            t_substMap.put(t_subst.getSubstituentType(), t_int+1);
                        }
                        this.m_handledResidues.put(t_node, true);
                    }
                }
            }
            Collections.sort(t_substList, new SubstComparator());
            for (SubstituentType t_substituentType : t_substList)
            {
                Integer t_int = t_substMap.get(t_substituentType);
                if ( t_int > 1 )
                {
                    t_name += "-(" + t_int.toString() + ")" + t_substituentType.getName();
                }
                else
                {
                    t_name += "-" + t_substituentType.getName();
                }
            }
            Integer t_int = this.m_composition.get(t_name);
            if ( t_int == null )
            {
                this.m_composition.put(t_name, 1);
            }
            else
            {
                this.m_composition.put(t_name, t_int+1);
            }
            this.m_handledResidues.put(a_ms, true);
        }
    }

    public void visit(Substituent a_subst) throws GlycoVisitorException
    {
        if ( this.m_handledResidues.get(a_subst) == null )
        {
            Integer t_int = this.m_composition.get(a_subst.getSubstituentType().getName());
            if ( t_int == null )
            {
                this.m_composition.put(a_subst.getSubstituentType().getName(), 1);
            }
            else
            {
                this.m_composition.put(a_subst.getSubstituentType().getName(), 1+t_int);
            }
            this.m_handledResidues.put(a_subst, true);
        }
    }

}
