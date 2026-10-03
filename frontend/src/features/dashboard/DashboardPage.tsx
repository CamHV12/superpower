import { useEffect, useMemo, useState } from 'react';
import { financeService } from '../finance/finance.service';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Badge } from '../components/ui/Badge';
import { Card } from '../components/ui/Card';

const revenueData = [] as { month: string; paidAmount: number }[];


const activities = [
  ['Nguyễn Minh Anh', 'Tạo dự án Website E-commerce', '10 phút trước', 'info'],
  ['Trần Quốc Bảo', 'Hoàn thành task Thiết kế database', '32 phút trước', 'success'],
  ['Lê Thu Hà', 'Tạo hóa đơn INV-2026-018', '1 giờ trước', 'warning'],
  ['Phạm Đức Long', 'Cập nhật tiến độ Mobile App lên 80%', '2 giờ trước', 'info'],
] as const;

const formatMoney = (value: number) => new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND',
  maximumFractionDigits: 0,
});


