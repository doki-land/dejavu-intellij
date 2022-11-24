package dejavu.intellij.language

/**
 * 模板配置类，支持多种定界符和空白控制符
 */
data class TemplateConfig(
    // 基本定界符
    val slotStart: String = "<%",
    val slotEnd: String = "%>",
    val commentStart: String = "<#",
    val commentEnd: String = "#>",
    val allowPipeOperator: Boolean = false
) {
    companion object {
        // 开始定界符集合
        const val SLOT_START_BASIC = "<%"           // 基本
        const val SLOT_START_OUTPUT = "<%="          // 输出表达式
        const val SLOT_START_TRIM_BEFORE = "<%_"     // 去除前面空白
        const val SLOT_START_TRIM_AFTER = "<%-"      // 去除后面空白
        const val SLOT_START_TRIM_BOTH = "<%~"       // 去除前后空白

        // 结束定界符集合
        const val SLOT_END_BASIC = "%>"              // 基本
        const val SLOT_END_TRIM_BEFORE = "_%>"       // 去除前面空白
        const val SLOT_END_TRIM_AFTER = "-%>"        // 去除后面空白
        const val SLOT_END_TRIM_BOTH = "~%>"         // 去除前后空白

        // 所有开始定界符，按长度降序排列（长的优先匹配）
        val ALL_SLOT_STARTS = listOf(
            SLOT_START_TRIM_BOTH,    // <%~  (3 chars)
            SLOT_START_OUTPUT,       // <%=  (3 chars)
            SLOT_START_TRIM_BEFORE,  // <%_  (3 chars)
            SLOT_START_TRIM_AFTER,   // <%-  (3 chars)
            SLOT_START_BASIC         // <%   (2 chars)
        )

        // 所有结束定界符，按长度降序排列（长的优先匹配）
        val ALL_SLOT_ENDS = listOf(
            SLOT_END_TRIM_BOTH,      // ~%>  (3 chars)
            SLOT_END_TRIM_BEFORE,    // _%>  (3 chars)
            SLOT_END_TRIM_AFTER,     // -%>  (3 chars)
            SLOT_END_BASIC           // %>   (2 chars)
        )
    }
}