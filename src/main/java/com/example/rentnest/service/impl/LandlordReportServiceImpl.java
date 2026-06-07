package com.example.rentnest.service.impl;

import com.example.rentnest.model.dto.response.InvoiceStatsResponse;
import com.example.rentnest.model.dto.response.LandlordReportOverviewResponse;
import com.example.rentnest.repository.InvoiceItemRepository;
import com.example.rentnest.repository.InvoiceRepository;
import com.example.rentnest.service.InvoiceService;
import com.example.rentnest.service.LandlordReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LandlordReportServiceImpl implements LandlordReportService {

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;
    @Autowired
    private InvoiceService invoiceService;

    @Override
    public LandlordReportOverviewResponse getOverview(Long landlordID, String invoiceMonth) {
        InvoiceStatsResponse stats = invoiceService.getStatsForLandlord(landlordID, invoiceMonth);
        List<LandlordReportOverviewResponse.CashFlowRow> cashFlowRows = buildCashFlowRows(landlordID, invoiceMonth);
        List<LandlordReportOverviewResponse.RevenueSlice> revenueSlices = buildRevenueSlice(landlordID, invoiceMonth);
        BigDecimal totalRevenue = cashFlowRows.stream().map(LandlordReportOverviewResponse.CashFlowRow::getTotalRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return LandlordReportOverviewResponse.builder()
                .invoiceMonth(invoiceMonth)
                .totalInvoices(stats.getTotalInvoices())
                .totalRevenue(totalRevenue)
                .paidAmount(stats.getPaidAmount())
                .debtAmount(stats.getDebtAmount())
                .revenueStracture(revenueSlices)
                .cashFlowByHostel(cashFlowRows)
                .build();

    }

    private List<LandlordReportOverviewResponse.RevenueSlice> buildRevenueSlice(Long landlordID, String invoiceMonth) {
        List<Object[]> revenueSlices = invoiceItemRepository.summarizeRevenueStructure(landlordID, invoiceMonth);
        List<LandlordReportOverviewResponse.RevenueSlice> revenueSliceList = new ArrayList<>();
        for (Object[] slices : revenueSlices) {
            LandlordReportOverviewResponse.RevenueSlice revenueSlice = new LandlordReportOverviewResponse.RevenueSlice();
            revenueSlice.setLabel((String) slices[0]);
            revenueSlice.setAmount((BigDecimal) slices[1]);
            revenueSliceList.add(revenueSlice);

        }
        return revenueSliceList;
    }



    private List<LandlordReportOverviewResponse.CashFlowRow> buildCashFlowRows(Long landlordID, String invoiceMonth) {
        List<Object[]> cashFlowRows = invoiceItemRepository.summarizeCashflowByHostel(landlordID, invoiceMonth);
        List<LandlordReportOverviewResponse.CashFlowRow> cashFlowRowList = new ArrayList<>();
        for (Object[] row : cashFlowRows) {
            LandlordReportOverviewResponse.CashFlowRow cashFlowRow = new LandlordReportOverviewResponse.CashFlowRow();
            cashFlowRow.setHostelID((Long) row[0]);
            cashFlowRow.setHostelName((String) row[1]);
            cashFlowRow.setRoomRevenue(row[2] == null ? (BigDecimal.ZERO) : new BigDecimal(String.valueOf(row[2])));
            cashFlowRow.setServiceRevenue(row[3] == null ? (BigDecimal.ZERO) : new BigDecimal(String.valueOf(row[3])));
            cashFlowRow.setTotalRevenue(row[4] == null ? (BigDecimal.ZERO) : new BigDecimal(String.valueOf(row[4])));
            cashFlowRow.setExpenseAmount(BigDecimal.ZERO);
            cashFlowRow.setGrossProfit(cashFlowRow.getTotalRevenue().subtract(cashFlowRow.getExpenseAmount()));
            cashFlowRowList.add(cashFlowRow);
        }
        return cashFlowRowList;
    }

}
