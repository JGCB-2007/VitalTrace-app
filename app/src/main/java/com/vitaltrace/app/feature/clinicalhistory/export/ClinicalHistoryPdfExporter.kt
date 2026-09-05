package com.vitaltrace.app.feature.clinicalhistory.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ClinicalHistoryPdfExporter {
    fun export(
        context: Context,
        uri: Uri,
        patientName: String,
        history: ClinicalHistory
    ): Result<Unit> = runCatching {
        val safeName = clean(patientName).ifBlank { "Paciente" }
        val safeRecord = clean(history.recordNumber)
        val totalPages = measurePages(safeName, safeRecord, history)
        val document = PdfDocument()
        try {
            val template = ClinicalHistoryTemplate(document, safeName, safeRecord, totalPages)
            render(template, history)
            template.finish()
            context.contentResolver.openOutputStream(uri)?.use(document::writeTo)
                ?: error("No se pudo abrir el archivo seleccionado.")
        } finally {
            document.close()
        }
    }

    private fun measurePages(name: String, record: String, history: ClinicalHistory): Int {
        val draft = PdfDocument()
        return try {
            val template = ClinicalHistoryTemplate(draft, name, record, null)
            render(template, history)
            template.finish()
            template.pageCount
        } finally {
            draft.close()
        }
    }

    private fun render(template: ClinicalHistoryTemplate, history: ClinicalHistory) {
        template.patientOverview(
            diagnoses = history.diagnoses.size,
            treatments = history.currentTreatments.size,
            measurements = history.recentMeasurements.size,
            evolutions = history.clinicalEvolutions.size
        )

        template.section("Diagnósticos", SectionIcon.DIAGNOSIS)
        if (history.diagnoses.isEmpty()) template.empty()
        history.diagnoses.chunked(2).forEach { row ->
            template.diagnosisRow(row.map { diagnosis ->
                ReportCard(
                    title = clean(diagnosis.description),
                    code = diagnosis.cieCode?.let(::clean).orEmpty(),
                    date = clean(diagnosis.diagnosisDate),
                    status = statusLabel(diagnosis.status)
                )
            })
        }

        template.section("Tratamientos actuales", SectionIcon.TREATMENT)
        template.table(
            headers = listOf("Tratamiento", "Inicio", "Estado"),
            widths = listOf(0.58f, 0.22f, 0.20f),
            rows = history.currentTreatments.map {
                listOf(clean(it.indications), clean(it.startDate), statusLabel(it.status))
            },
            statusColumn = 2
        )

        template.section("Mediciones recientes", SectionIcon.MEASUREMENT)
        template.table(
            headers = listOf("Tipo", "Valor", "Unidad", "Fecha y hora"),
            widths = listOf(0.35f, 0.15f, 0.15f, 0.35f),
            rows = history.recentMeasurements.map {
                listOf(
                    clean(it.measurementType?.name ?: "Medición"),
                    clean(it.value.toString()),
                    clean(it.unit),
                    clean(it.measuredAt)
                )
            }
        )

        template.section("Evoluciones clínicas", SectionIcon.EVOLUTION)
        if (history.clinicalEvolutions.isEmpty()) template.empty()
        history.clinicalEvolutions.forEach { evolution ->
            template.evolutionCard(
                title = clean(evolution.clinicalSummary),
                date = clean(evolution.recordedAt),
                status = statusLabel(evolution.status)
            )
        }
    }

    private fun clean(value: String): String = value
        .replace(0xFFFE.toChar(), '.')
        .replace(0xFFFF.toChar(), '.')
        .replace(0xFFFD.toChar(), '.')
        .filter { it.code !in 0..8 && it.code !in 11..12 && it.code !in 14..31 }
        .trim()

    private fun statusLabel(value: String): String = when (value.trim().uppercase()) {
        "ACTIVE" -> "Activo"
        "RESOLVED" -> "Resuelto"
        "UNDER_REVIEW" -> "En revisión"
        "STABLE" -> "Estable"
        "OBSERVATION" -> "En observación"
        "DELICATE" -> "Delicado"
        "CRITICAL" -> "Crítico"
        "RECOVERY" -> "En recuperación"
        else -> clean(value)
    }
}

private data class ReportCard(
    val title: String,
    val code: String,
    val date: String,
    val status: String
)

private enum class SectionIcon { DIAGNOSIS, TREATMENT, MEASUREMENT, EVOLUTION }

private class ClinicalHistoryTemplate(
    private val document: PdfDocument,
    private val patientName: String,
    private val recordNumber: String,
    private val totalPages: Int?
) {
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 38f
    private val contentWidth = pageWidth - margin * 2
    private val footerTop = 806f
    private var page: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var y = 0f
    var pageCount: Int = 0
        private set

    private val generatedAt = LocalDateTime.now()
    private val generatedDate = generatedAt.format(DateTimeFormatter.ofPattern("d MMM uuuu", Locale.getDefault()))
    private val generatedTime = generatedAt.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))

    private val navy = Color.rgb(23, 44, 58)
    private val teal = Color.rgb(24, 137, 133)
    private val mint = Color.rgb(221, 244, 242)
    private val warm = Color.rgb(250, 248, 244)
    private val white = Color.WHITE
    private val border = Color.rgb(226, 232, 232)
    private val shadow = Color.rgb(231, 230, 226)
    private val muted = Color.rgb(83, 99, 109)
    private val green = Color.rgb(35, 128, 95)
    private val greenSoft = Color.rgb(221, 241, 231)

    private val brandPaint = textPaint(navy, 20f, true, Typeface.SERIF)
    private val reportPaint = textPaint(navy, 16f, true, Typeface.SERIF)
    private val patientPaint = textPaint(navy, 22f, true, Typeface.SERIF)
    private val sectionPaint = textPaint(navy, 15f, true, Typeface.SERIF)
    private val bodyPaint = textPaint(navy, 9.5f)
    private val bodyBoldPaint = textPaint(navy, 10f, true)
    private val labelPaint = textPaint(muted, 8f)
    private val labelBoldPaint = textPaint(muted, 8.5f, true)
    private val whiteBoldPaint = textPaint(white, 8.5f, true)
    private val statusPaint = textPaint(green, 8f, true)

    fun patientOverview(diagnoses: Int, treatments: Int, measurements: Int, evolutions: Int) {
        ensurePage()
        val top = y
        val leftWidth = 330f
        val gap = 14f
        val rightLeft = margin + leftWidth + gap
        val rightWidth = contentWidth - leftWidth - gap
        elevatedCard(RectF(margin, top, margin + leftWidth, top + 128f), 16f)
        elevatedCard(RectF(rightLeft, top, pageWidth - margin, top + 128f), 16f)

        canvas!!.drawCircle(margin + 48f, top + 48f, 27f, fill(mint))
        drawCentered(initials(patientName), margin + 48f, top + 53f, textPaint(teal, 13f, true))
        val nameLines = wrap(patientName, patientPaint, 225f).take(2)
        var nameY = top + 36f
        nameLines.forEach { line ->
            canvas!!.drawText(line, margin + 88f, nameY, patientPaint)
            nameY += 24f
        }
        canvas!!.drawText("EXPEDIENTE", margin + 20f, top + 89f, labelBoldPaint)
        canvas!!.drawText(recordNumber.ifBlank { "—" }, margin + 20f, top + 105f, bodyBoldPaint)
        canvas!!.drawText("GENERADO", margin + 142f, top + 89f, labelBoldPaint)
        canvas!!.drawText("$generatedDate · $generatedTime", margin + 142f, top + 105f, bodyPaint)

        canvas!!.drawText("Resumen general", rightLeft + 16f, top + 24f, reportPaint)
        summaryLine("Edad", "No disponible", rightLeft + 16f, top + 48f, rightWidth - 32f)
        summaryLine("Sexo", "No disponible", rightLeft + 16f, top + 70f, rightWidth - 32f)
        summaryLine("Expediente", recordNumber.ifBlank { "—" }, rightLeft + 16f, top + 92f, rightWidth - 32f)

        y = top + 146f
        val metrics = listOf("Diagnósticos" to diagnoses, "Tratamientos" to treatments, "Mediciones" to measurements, "Evoluciones" to evolutions)
        val metricGap = 8f
        val metricWidth = (contentWidth - metricGap * 3) / 4
        metrics.forEachIndexed { index, metric ->
            val left = margin + index * (metricWidth + metricGap)
            canvas!!.drawRoundRect(RectF(left, y, left + metricWidth, y + 47f), 11f, 11f, fill(mint))
            canvas!!.drawText(metric.second.toString(), left + 12f, y + 20f, reportPaint)
            drawFitted(metric.first, left + 12f, y + 36f, metricWidth - 24f, labelBoldPaint)
        }
        y += 68f
    }

    fun section(title: String, icon: SectionIcon) {
        ensure(100f)
        drawSectionIcon(icon, margin + 12f, y + 10f)
        canvas!!.drawText(title, margin + 34f, y + 15f, sectionPaint)
        canvas!!.drawLine(margin + 34f, y + 23f, pageWidth - margin, y + 23f, stroke(border, 1f))
        y += 38f
    }

    fun empty() {
        ensure(44f)
        elevatedCard(RectF(margin, y, pageWidth - margin, y + 34f), 11f)
        canvas!!.drawCircle(margin + 17f, y + 17f, 6f, fill(mint))
        canvas!!.drawText("Sin información registrada.", margin + 31f, y + 21f, labelBoldPaint)
        y += 48f
    }

    fun diagnosisRow(cards: List<ReportCard>) {
        val gap = 12f
        val cardWidth = (contentWidth - gap) / 2
        val heights = cards.map { diagnosisHeight(it, cardWidth) }
        val height = heights.maxOrNull() ?: 92f
        ensure(height + 12f)
        cards.forEachIndexed { index, item ->
            diagnosisCard(item, margin + index * (cardWidth + gap), y, cardWidth, height)
        }
        y += height + 12f
    }

    fun evolutionCard(title: String, date: String, status: String) {
        val lines = wrap(title, bodyPaint, contentWidth - 40f)
        val height = maxOf(76f, 49f + lines.size * 12f)
        ensure(height + 10f)
        elevatedCard(RectF(margin, y, pageWidth - margin, y + height), 14f)
        canvas!!.drawRoundRect(RectF(margin, y, margin + 5f, y + height), 3f, 3f, fill(teal))
        var textY = y + 22f
        lines.forEach { value ->
            canvas!!.drawText(value, margin + 18f, textY, bodyBoldPaint)
            textY += 12f
        }
        canvas!!.drawText(date, margin + 18f, y + height - 15f, labelPaint)
        drawChip(status, pageWidth - margin - 89f, y + height - 29f, 77f)
        y += height + 10f
    }

    fun table(
        headers: List<String>,
        widths: List<Float>,
        rows: List<List<String>>,
        statusColumn: Int? = null
    ) {
        if (rows.isEmpty()) return empty()
        ensure(68f)
        tableHeader(headers, widths)
        rows.forEach { values ->
            val lineCounts = values.mapIndexed { index, value ->
                wrap(value, bodyPaint, contentWidth * widths[index] - 14f).size
            }
            val rowHeight = maxOf(34f, (lineCounts.maxOrNull() ?: 1) * 12f + 16f)
            if (ensure(rowHeight + 31f)) tableHeader(headers, widths)
            tableRow(values, widths, rowHeight, statusColumn)
        }
        y += 10f
    }

    fun finish() = finishPage()

    private fun diagnosisHeight(item: ReportCard, width: Float): Float {
        val titleLines = wrap(item.title, bodyBoldPaint, width - 30f).size
        return maxOf(94f, 65f + titleLines * 12f)
    }

    private fun diagnosisCard(item: ReportCard, left: Float, top: Float, width: Float, height: Float) {
        elevatedCard(RectF(left, top, left + width, top + height), 14f)
        canvas!!.drawRoundRect(RectF(left + 14f, top + 13f, left + 40f, top + 39f), 8f, 8f, fill(mint))
        drawMedicalCross(left + 27f, top + 26f, teal)
        var textY = top + 54f
        wrap(item.title, bodyBoldPaint, width - 28f).forEach { value ->
            canvas!!.drawText(value, left + 14f, textY, bodyBoldPaint)
            textY += 12f
        }
        if (item.code.isNotBlank()) canvas!!.drawText("CIE ${item.code}", left + 14f, top + height - 29f, labelBoldPaint)
        canvas!!.drawText(item.date, left + 14f, top + height - 14f, labelPaint)
        drawChip(item.status, left + width - 82f, top + 16f, 68f)
    }

    private fun tableHeader(headers: List<String>, widths: List<Float>) {
        canvas!!.drawRoundRect(RectF(margin, y, pageWidth - margin, y + 29f), 10f, 10f, fill(mint))
        var left = margin
        headers.forEachIndexed { index, header ->
            canvas!!.drawText(header, left + 8f, y + 19f, labelBoldPaint)
            left += contentWidth * widths[index]
        }
        y += 29f
    }

    private fun tableRow(values: List<String>, widths: List<Float>, height: Float, statusColumn: Int?) {
        val top = y
        canvas!!.drawRect(RectF(margin, top, pageWidth - margin, top + height), fill(white))
        var left = margin
        values.forEachIndexed { index, value ->
            val cellWidth = contentWidth * widths[index]
            if (index == statusColumn) {
                drawChip(value, left + 7f, top + 7f, cellWidth - 14f)
            } else {
                var baseline = top + 17f
                wrap(value, bodyPaint, cellWidth - 14f).forEach { lineValue ->
                    canvas!!.drawText(lineValue, left + 7f, baseline, bodyPaint)
                    baseline += 12f
                }
            }
            if (index > 0) canvas!!.drawLine(left, top + 6f, left, top + height - 6f, stroke(border, 0.7f))
            left += cellWidth
        }
        canvas!!.drawLine(margin, top + height, pageWidth - margin, top + height, stroke(border, 0.8f))
        y += height
    }

    private fun summaryLine(label: String, value: String, left: Float, baseline: Float, width: Float) {
        canvas!!.drawText(label, left, baseline, labelPaint)
        drawFitted(value, left + 58f, baseline, width - 58f, bodyBoldPaint)
    }

    private fun elevatedCard(rect: RectF, radius: Float) {
        val shadowRect = RectF(rect.left, rect.top + 3f, rect.right, rect.bottom + 3f)
        canvas!!.drawRoundRect(shadowRect, radius, radius, fill(shadow))
        canvas!!.drawRoundRect(rect, radius, radius, fill(white))
        canvas!!.drawRoundRect(rect, radius, radius, stroke(border, 0.7f))
    }

    private fun drawChip(value: String, left: Float, top: Float, width: Float) {
        val safeWidth = width.coerceAtLeast(48f)
        canvas!!.drawRoundRect(RectF(left, top, left + safeWidth, top + 20f), 10f, 10f, fill(greenSoft))
        drawFitted(value, left + 7f, top + 14f, safeWidth - 14f, statusPaint)
    }

    private fun drawSectionIcon(icon: SectionIcon, centerX: Float, centerY: Float) {
        canvas!!.drawCircle(centerX, centerY, 11f, fill(teal))
        when (icon) {
            SectionIcon.DIAGNOSIS -> drawMedicalCross(centerX, centerY, white)
            SectionIcon.TREATMENT -> {
                canvas!!.drawRoundRect(RectF(centerX - 6f, centerY - 2f, centerX + 6f, centerY + 2f), 2f, 2f, fill(white))
                canvas!!.drawCircle(centerX - 5f, centerY, 2f, fill(mint))
            }
            SectionIcon.MEASUREMENT -> drawPulse(centerX - 7f, centerY, white, 1.5f)
            SectionIcon.EVOLUTION -> {
                canvas!!.drawLine(centerX - 5f, centerY - 4f, centerX + 5f, centerY - 4f, stroke(white, 1.5f))
                canvas!!.drawLine(centerX - 5f, centerY, centerX + 5f, centerY, stroke(white, 1.5f))
                canvas!!.drawLine(centerX - 5f, centerY + 4f, centerX + 2f, centerY + 4f, stroke(white, 1.5f))
            }
        }
    }

    private fun drawMedicalCross(centerX: Float, centerY: Float, color: Int) {
        canvas!!.drawRoundRect(RectF(centerX - 5f, centerY - 1.6f, centerX + 5f, centerY + 1.6f), 1f, 1f, fill(color))
        canvas!!.drawRoundRect(RectF(centerX - 1.6f, centerY - 5f, centerX + 1.6f, centerY + 5f), 1f, 1f, fill(color))
    }

    private fun drawPulse(startX: Float, centerY: Float, color: Int, strokeWidth: Float) {
        val path = Path().apply {
            moveTo(startX, centerY)
            lineTo(startX + 4f, centerY)
            lineTo(startX + 6f, centerY - 5f)
            lineTo(startX + 9f, centerY + 5f)
            lineTo(startX + 12f, centerY)
            lineTo(startX + 16f, centerY)
        }
        canvas!!.drawPath(path, stroke(color, strokeWidth))
    }

    private fun drawCentered(value: String, centerX: Float, baseline: Float, paint: Paint) {
        canvas!!.drawText(value, centerX - paint.measureText(value) / 2f, baseline, paint)
    }

    private fun drawFitted(value: String, x: Float, baseline: Float, maxWidth: Float, source: Paint) {
        val target = Paint(source)
        while (target.measureText(value) > maxWidth && target.textSize > 6f) target.textSize -= 0.5f
        canvas!!.drawText(value, x, baseline, target)
    }

    private fun wrap(value: String, source: Paint, maxWidth: Float): List<String> {
        if (value.isBlank()) return listOf("—")
        val result = mutableListOf<String>()
        var current = ""
        value.replace(10.toChar(), ' ').replace(13.toChar(), ' ')
            .split(Regex(" +"))
            .forEach { word ->
                val candidate = if (current.isEmpty()) word else "$current $word"
                if (source.measureText(candidate) <= maxWidth || current.isEmpty()) current = candidate
                else { result += current; current = word }
            }
        if (current.isNotEmpty()) result += current
        return result
    }

    private fun ensure(required: Float): Boolean {
        ensurePage()
        if (y + required <= footerTop - 12f) return false
        finishPage()
        ensurePage()
        return true
    }

    private fun ensurePage() {
        if (page != null) return
        page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++pageCount).create())
        canvas = page!!.canvas
        canvas!!.drawColor(warm)
        drawPageHeader()
        y = 91f
    }

    private fun drawPageHeader() {
        canvas!!.drawRoundRect(RectF(margin, 22f, margin + 34f, 56f), 11f, 11f, fill(mint))
        drawPulse(margin + 9f, 39f, teal, 2f)
        canvas!!.drawText("VitalTrace", margin + 44f, 44f, brandPaint)
        canvas!!.drawText("Expediente clínico", 355f, 38f, reportPaint)
        canvas!!.drawText("Generado $generatedDate · $generatedTime", 355f, 54f, labelPaint)
        canvas!!.drawLine(margin, 70f, pageWidth - margin, 70f, stroke(border, 1f))
    }

    private fun finishPage() {
        val current = page ?: return
        val pageCanvas = canvas!!
        pageCanvas.drawLine(margin, footerTop, pageWidth - margin, footerTop, stroke(border, 1f))
        pageCanvas.drawText(
            "Documento generado automáticamente. La información aquí contenida es confidencial.",
            margin,
            footerTop + 17f,
            labelPaint
        )
        val pageLabel = totalPages?.let { "Página $pageCount de $it" } ?: "Página $pageCount"
        drawFitted(pageLabel, pageWidth - margin - 62f, footerTop + 17f, 62f, labelBoldPaint)
        document.finishPage(current)
        page = null
        canvas = null
    }

    private fun initials(value: String): String = value
        .split(' ')
        .filter(String::isNotBlank)
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifBlank { "VT" }

    private fun textPaint(color: Int, size: Float, bold: Boolean = false, family: Typeface = Typeface.DEFAULT) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = size
            typeface = Typeface.create(family, if (bold) Typeface.BOLD else Typeface.NORMAL)
        }

    private fun fill(color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }

    private fun stroke(color: Int, width: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.STROKE
        strokeWidth = width
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
}
