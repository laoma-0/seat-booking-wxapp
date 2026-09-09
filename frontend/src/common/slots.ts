/**
 * 时段常量与工具（首页、座位页共用，避免两处定义不一致）
 */

export interface TimeSlot {
	label: string      // 展示文案，如 08:00-12:00
	startTime: string  // 传给后端的开始时间，HH:mm:ss
	endTime: string    // 传给后端的结束时间，HH:mm:ss
}

export const TIME_SLOTS: TimeSlot[] = [
	{ label: '08:00-12:00', startTime: '08:00:00', endTime: '12:00:00' },
	{ label: '12:00-15:00', startTime: '12:00:00', endTime: '15:00:00' },
	{ label: '15:00-19:00', startTime: '15:00:00', endTime: '19:00:00' },
	{ label: '19:00-22:00', startTime: '19:00:00', endTime: '22:00:00' },
]

/** 今天 yyyy-MM-dd */
export function todayStr(): string {
	const d = new Date()
	const m = String(d.getMonth() + 1).padStart(2, '0')
	const day = String(d.getDate()).padStart(2, '0')
	return `${d.getFullYear()}-${m}-${day}`
}

/** 该场次今天的开始时刻（毫秒）。当前只支持约今天，支持多天时改用 bookDate 拼接 */
export function slotStart(s: TimeSlot): number {
	const [h, m] = s.startTime.split(':').map(Number)
	const d = new Date()
	d.setHours(h, m, 0, 0)
	return d.getTime()
}

/** 该场次是否已开始（已开始则不可预约） */
export function isPast(s: TimeSlot): boolean {
	return slotStart(s) <= Date.now()
}

/** 第一个还没开始的时段下标；全部已开始时返回 0 */
export function firstAvailableSlot(): number {
	const i = TIME_SLOTS.findIndex(s => !isPast(s))
	return i === -1 ? 0 : i
}
