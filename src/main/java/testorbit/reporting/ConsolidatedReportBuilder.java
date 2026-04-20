package testorbit.reporting;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Consolidated Device-Wise HTML Report
 * Shows test execution breakdown by device
 */
public class ConsolidatedReportBuilder {

    public static void generateReport(Map<String, DeviceStats> deviceStats) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            
        // Save consolidated report in reports/consolidated/Device_summary<timestamp>.html
        String reportFolder = "reports/consolidated";
        String reportPath = reportFolder + "/Device_summary" + timestamp + ".html";

        // Create directory if it doesn't exist
        File reportDir = new File(reportFolder);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
            System.out.println("📊 Reports folder created: " + reportFolder);
        }

        try (FileWriter writer = new FileWriter(reportPath)) {
            writer.write(generateHTML(deviceStats));
            System.out.println("✓ Device-Wise report: " + reportPath);
        } catch (Exception e) {
            System.err.println("Failed to generate consolidated report: " + e.getMessage());
        }
    }

    private static String generateHTML(Map<String, DeviceStats> stats) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html><html><head>");
        html.append("<title>Device-Wise Test Report</title>");
        html.append("<style>");
        html.append("body { font-family: Arial; margin: 20px; background: #f5f5f5; }");
        html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); ");
        html.append("color: white; padding: 30px; border-radius: 10px; text-align: center; }");
        html.append(".device-card { background: white; margin: 20px 0; padding: 20px; ");
        html.append("border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }");
        html.append(".stat-box { padding: 15px; background: #f8f9fa; border-radius: 5px; text-align: center; }");
        html.append(".pass { color: #28a745; } .fail { color: #dc3545; }");
        html.append("</style></head><body>");
        
        // Header
        html.append("<div class='header'><h1>📱 Device-Wise Test Execution Report</h1>");
        html.append("<p>Generated: ").append(LocalDateTime.now()).append("</p></div>");
        
        // Device cards
        stats.forEach((deviceName, deviceStats) -> {
            html.append("<div class='device-card'>");
            html.append("<h2>").append(deviceName).append("</h2>");
            html.append("<div class='stats'>");
            html.append("<div class='stat-box'><h3>").append(deviceStats.total).append("</h3><p>Total Tests</p></div>");
            html.append("<div class='stat-box'><h3 class='pass'>").append(deviceStats.passed).append("</h3><p>Passed</p></div>");
            html.append("<div class='stat-box'><h3 class='fail'>").append(deviceStats.failed).append("</h3><p>Failed</p></div>");
            html.append("<div class='stat-box'><h3>").append(String.format("%.1f%%", deviceStats.getPassRate())).append("</h3><p>Pass Rate</p></div>");
            html.append("</div></div>");
        });
        
        html.append("</body></html>");
        return html.toString();
    }

    public static class DeviceStats {
        int total, passed, failed;
        
        public double getPassRate() {
            return total > 0 ? (double) passed / total * 100 : 0;
        }
    }
}
