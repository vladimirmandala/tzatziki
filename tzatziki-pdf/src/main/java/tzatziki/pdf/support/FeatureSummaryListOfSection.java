package tzatziki.pdf.support;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import gutenberg.itext.ITextContext;
import gutenberg.itext.Sections;
import gutenberg.itext.SimpleEmitter;
import gutenberg.itext.Styles;
import tzatziki.analysis.exec.model.FeatureExec;
import tzatziki.analysis.exec.model.ScenarioExec;
import tzatziki.analysis.exec.model.Status;
import tzatziki.pdf.emitter.StatusMarker;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class FeatureSummaryListOfSection implements SimpleEmitter {
    private final List<FeatureExec> features;
    private final int hLevel;
    private StatusMarker statusMarker = new StatusMarker();
    private boolean debugTable = false;

    public FeatureSummaryListOfSection(List<FeatureExec> features, int hLevel) {
        this.features = features;
        this.hLevel = hLevel;
    }

    @Override
    public void emit(ITextContext emitterContext) {
        for (FeatureExec feature : features) {
            emitFeature(feature, emitterContext);
        }
    }

    private void emitFeature(FeatureExec feature, ITextContext emitterContext) {
        Styles styles = emitterContext.styles();
        Sections sections = emitterContext.sections();

        Section featureSection = sections.newSection(feature.name(), hLevel);
        try {
            PdfPTable table = new PdfPTable(new float[]{1f, 22f});
            for (ScenarioExec scenarioExec : feature.scenario()) {
                table.addCell(statusCell(scenarioExec.status(), styles));
                table.addCell(titleCell(scenarioExec, styles));
            }
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);
            featureSection.add(table);
        } finally {
            sections.leaveSection(hLevel);
        }
    }

    private PdfPCell titleCell(ScenarioExec scenarioExec, Styles styles) {
        Font font = styles.defaultFont();
        Paragraph p = new Paragraph(scenarioExec.name(), font);
        PdfPCell cell = new PdfPCell(p);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        if (!debugTable)
            cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell statusCell(Status status, Styles styles) {
        Phrase statusSymbol = new Phrase(statusMarker.statusMarker(status));
        PdfPCell statusCell = new PdfPCell(statusSymbol);
        statusCell.setVerticalAlignment(Element.ALIGN_TOP);
        statusCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (!debugTable)
            statusCell.setBorder(Rectangle.NO_BORDER);
        return statusCell;
    }
}
