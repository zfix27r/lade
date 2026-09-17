package app.lade.ui.gesture

import androidx.compose.animation.core.Spring

/**
 * Настройки «магнитного» поведения: когда анимация в промежуточном состоянии,
 * система доводит её до 0 или 1 в зависимости от порога.
 *
 * @param collapseThreshold Порог для сворачивания (direction = COLLAPSE).
 *   Если progress больше этого значения — доведёт до 1 (свернуто),
 *   иначе — до 0 (развернуто). Увеличение делает сворачивание «ленивым»:
 *   нужно протянуть дальше, чтобы оно сработало. Диапазон 0f..1f.
 *
 * @param expandThreshold Порог для раскрытия (direction = EXPAND).
 *   Симметричен collapseThreshold, но для обратного направления.
 *   Обычно чуть меньше collapseThreshold, чтобы раскрытие было «охотнее»
 *   сворачивания (стандартный UX для сворачиваемых хедеров).
 *
 * @param stiffness Жёсткость пружины анимации. Чем больше — тем резче
 *   и быстрее доводит до края. Spring.StiffnessLow — медленно и мягко,
 *   Spring.StiffnessHigh — быстро и жёстко. Не влияет на порог.
 *
 * @param dampingRatio Затухание пружины. DampingRatioNoBouncy (1f) —
 *   без отскока. DampingRatioLowBouncy (0.2f) — заметный отскок.
 *   DampingRatioMediumBouncy (0.5f) — умеренный. Влияет на «игривость».
 *
 * @param enableHaptics Включает виброотклик в момент пересечения порога.
 *   Полезно для тактильного подтверждения смены состояния.
 *   На устройствах без вибро — no-op.
 */
data class SnapToEdgeConfig(
    val collapseThreshold: Float = 0.7f,
    val expandThreshold: Float = 0.4f,
    val stiffness: Float = Spring.StiffnessMediumLow,
    val dampingRatio: Float = Spring.DampingRatioNoBouncy,
    val enableHaptics: Boolean = true,
)