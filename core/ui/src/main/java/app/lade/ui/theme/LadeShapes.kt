package app.lade.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val LadeShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // чипы, теги
    small = RoundedCornerShape(12.dp),        // поля ввода
    medium = RoundedCornerShape(16.dp),       // кнопки, карточки — база
    large = RoundedCornerShape(24.dp),        // крупные карточки, модалки
    extraLarge = RoundedCornerShape(28.dp),   // bottom sheets, hero
)