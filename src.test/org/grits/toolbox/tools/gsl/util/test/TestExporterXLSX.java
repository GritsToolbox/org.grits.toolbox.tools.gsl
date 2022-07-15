package org.grits.toolbox.tools.gsl.util.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Picture;
import org.grits.toolbox.tools.gsl.util.io.excel.ExporterXLSXAbstract;
import org.grits.toolbox.utils.io.ExcelWriterHelper;

public class TestExporterXLSX {

	public static void main(String[] args) {
		try {
			ExporterXLSXTest t_export = ExporterXLSXTest.getExporter(TestResourcePath.RESOURCE_DIR+"test.xlsx");
			t_export.createBook();
			t_export.write();
			t_export.closeBook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static class ExporterXLSXTest extends ExporterXLSXAbstract {

		public ExporterXLSXTest(String a_strOutputFile) throws IOException {
			super(a_strOutputFile);
		}

		public static ExporterXLSXTest getExporter(String a_strFilename) throws IOException {
			return new ExporterXLSXTest(a_strFilename);
		}

		@Override
		public boolean createBook() {
			this.m_sheetCurent = this.createSheet("Test");
			List<String> t_lGWBSeqs = new ArrayList<>();
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p/#ycleavage$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p(--??1D-Gal,p--??2D-NeuAc,p)--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p(--??1D-Gal,p--??2D-NeuAc,p/#ycleavage)--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p(--??1D-Gal,p--??2D-NeuAc,p)--??2D-NeuAc,p/#ycleavage$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p)--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p)--??2D-NeuAc,p/#ycleavage$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p/#ycleavage)--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p--??2D-NeuAc,p/#ycleavage$MONO,perMe,0,0,freeEnd");
			t_lGWBSeqs.add("freeEnd/#bcleavage--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
			try {
				ExcelWriterHelper t_ewh = new ExcelWriterHelper();
				List<BufferedImage> t_lImages = new ArrayList<>();
				for ( String t_strGWBSeq : t_lGWBSeqs ) {
					String t_strGWBSeqWithLipid = t_strGWBSeq.replace(",freeEnd", ",Cer(d18:0/16:0)=0.0000u");
					t_lImages.add(t_ewh.createGlycanImage(t_strGWBSeqWithLipid, null, false, true, 0.5d));
					t_lImages.add(t_ewh.createGlycanImage(t_strGWBSeq, null, false, true, 0.5d));
					String t_strGWBSeqCutRedEnd = t_strGWBSeq.replace("freeEnd--", "freeEnd/#ccleavage--");
					t_lImages.add(t_ewh.createGlycanImage(t_strGWBSeqCutRedEnd, null, false, true, 0.5d));
				}
				BufferedImage t_img = t_ewh.createGlycanImage(t_lGWBSeqs, null, false, true, 0.5d);
				List<Picture> t_lPictures = new ArrayList<>();
				t_ewh.writeCellImage(this.m_book, this.m_sheetCurent, 0, 0, t_img, t_lPictures );
				int i = 1;
				for ( BufferedImage t_img0 : t_lImages ) {
					t_ewh.writeCellImage(this.m_book, this.m_sheetCurent, i, 0, t_img0, t_lPictures );
					t_ewh.writeCellImage(this.m_book, this.m_sheetCurent, 0, i, t_img0, t_lPictures );
					t_ewh.writeCellImage(this.m_book, this.m_sheetCurent, i, i, t_img0, t_lPictures );
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}
}
