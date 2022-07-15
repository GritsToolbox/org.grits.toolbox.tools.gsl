package org.grits.toolbox.tools.gsl.util.analyze.glycan;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.sugar.Anomer;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserNodes;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class GlycoVisitorTopology implements GlycoVisitor
{
    public void clear() 
    {
        // nothing to do
    }

    public GlycoTraverser getTraverser(GlycoVisitor a_visitor) throws GlycoVisitorException 
    {
        return new GlycoTraverserNodes(a_visitor);
    }

    public void start(Sugar a_sugar) throws GlycoVisitorException 
    {
        GlycoTraverser t_traverser = this.getTraverser(this);
        t_traverser.traverseGraph(a_sugar);
        if ( a_sugar.getUndeterminedSubTrees().size() != 0 ) 
        {
            throw new GlycoVisitorException("UnderdeterminedSubTree is not supported.");
        }
    }

    public void visit(Monosaccharide a_ms) throws GlycoVisitorException
    {
        try
        {
            if ( a_ms.getParentEdge() != null )
            {
                ArrayList<Integer> t_linkages = new ArrayList<Integer>();
                t_linkages.add(Linkage.UNKNOWN_POSITION);
                for (Linkage t_link : a_ms.getParentEdge().getGlycosidicLinkages()) 
                {
                    t_link.setParentLinkages(t_linkages);
                }
            }
            if ( a_ms.getAnomer().equals(Anomer.Alpha) || a_ms.getAnomer().equals(Anomer.Beta) )
            {
                a_ms.setAnomer(Anomer.Unknown);
            }
        }
        catch (Exception e)
        {
            throw new GlycoVisitorException(e.getMessage(),e);
        }
    }

    public void visit(NonMonosaccharide arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("NonMonosaccharide is not supported.");
    }

    public void visit(SugarUnitRepeat arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitRepeat is not supported.");
    }

    public void visit(Substituent arg0) throws GlycoVisitorException 
    {
        // nothing to do
    }

    public void visit(SugarUnitCyclic arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitCyclic is not supported.");
    }

    public void visit(SugarUnitAlternative arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitalternative is not supported.");
    }

    public void visit(UnvalidatedGlycoNode arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("UnvalidatedGlycoNode is not supported.");
    }

    public void visit(GlycoEdge a_edge) throws GlycoVisitorException 
    {
        // nothing to do
    }

}
