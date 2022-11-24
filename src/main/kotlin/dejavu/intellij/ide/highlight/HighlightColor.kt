package dejavu.intellij.ide.highlight

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.OptionsBundle
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.util.NlsContexts
import java.util.function.Supplier
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default
import dejavu.intellij.language.file.DejavuBundle
import dejavu.intellij.language.file.DejavuBundle.messagePointer as keyof


enum class HighlightColor(
    humanName: Supplier<@NlsContexts.AttributeDescriptor String>,
    default: TextAttributesKey? = null,
) {
    // 特殊关键词
    KEYWORD(keyof("color.settings.toml.keyword"), Default.KEYWORD),
    EXTENSION(OptionsBundle.messagePointer("options.language.defaults.metadata"), Default.METADATA),

    // 字面量
    NUMBER_HINT(keyof("color.settings.toml.number_hint"), Default.METADATA),
    DECIMAL(keyof("color.literal.decimal"), Default.NUMBER),
    INTEGER(keyof("color.literal.integer"), Default.NUMBER),
    STRING_HINT(keyof("color.settings.toml.string_hint"), Default.KEYWORD),
    STRING(OptionsBundle.messagePointer("options.language.defaults.string"), Default.STRING),
    STRING_ESCAPE(OptionsBundle.messagePointer("options.language.defaults.string"), Default.VALID_STRING_ESCAPE),
    IDENTIFIER(OptionsBundle.messagePointer("options.language.defaults.identifier"), Default.IDENTIFIER),

    //
    OPERATOR(keyof("color.token.operation"), Default.OPERATION_SIGN),
    OPTIONAL(keyof("color.token.optional"), KEYWORD.textAttributesKey),
    TAGGED(keyof("color.token.tagged"), OPERATOR.textAttributesKey),

    PREDEFINED(OptionsBundle.messagePointer("options.language.defaults.predefined"), Default.PREDEFINED_SYMBOL),
    TYPE_HINT(keyof("color.settings.toml.type_hint"), Default.CLASS_NAME),

    SYM_FUNCTION(keyof("color.symbol.function"), Default.STATIC_METHOD),
    SYM_PROPERTY(keyof("color.symbol.property"), Default.STATIC_FIELD),
    SYM_VARIABLE(keyof("color.symbol.variable"), Default.LOCAL_VARIABLE),

    SYM_MACRO(OptionsBundle.messagePointer("options.java.attribute.descriptor.annotation.name"), Default.METADATA),
    KEY_SYMBOL(keyof("color.symbol.builtin"), Default.STATIC_FIELD),

    // 标点符号
    PARENTHESES(OptionsBundle.messagePointer("options.language.defaults.parentheses"), Default.PARENTHESES),
    BRACKETS(OptionsBundle.messagePointer("options.language.defaults.brackets"), Default.BRACKETS),
    BRACES(OptionsBundle.messagePointer("options.language.defaults.braces"), Default.BRACES),
    DOT(OptionsBundle.messagePointer("options.language.defaults.dot"), Default.DOT),
    COMMA(OptionsBundle.messagePointer("options.language.defaults.comma"), Default.COMMA),
    SEMICOLON(OptionsBundle.messagePointer("options.language.defaults.semicolon"), Default.SEMICOLON),
    SET(keyof("color.token.set"), Default.OPERATION_SIGN),

    // 模板标记
    SLOT_MARK_LEVEL_0(keyof("color.mark.slot.level0"), Default.PARAMETER),
    SLOT_MARK_LEVEL_1(keyof("color.mark.slot.level1"), Default.LOCAL_VARIABLE),
    SLOT_MARK_LEVEL_2(keyof("color.mark.slot.level2"), Default.CLASS_NAME),
    SLOT_MARK_LEVEL_3(keyof("color.mark.slot.level3"), Default.INTERFACE_NAME),
    SLOT_MARK_LEVEL_4(keyof("color.mark.slot.level4"), Default.FUNCTION_DECLARATION),
    SLOT_MARK_LEVEL_5(keyof("color.mark.slot.level5"), Default.KEYWORD),

    // 注释
    COMMENT_INLINE(OptionsBundle.messagePointer("options.language.defaults.line.comment"), Default.LINE_COMMENT),
    COMMENT_BLOCK(OptionsBundle.messagePointer("options.language.defaults.block.comment"), Default.BLOCK_COMMENT),
    COMMENT_DOCUMENT(
        OptionsBundle.messagePointer("options.language.defaults.block.comment"),
        Default.DOC_COMMENT_MARKUP
    ),

    // 错误
    BAD_CHARACTER(
        OptionsBundle.messagePointer("options.java.attribute.descriptor.bad.character"),
        HighlighterColors.BAD_CHARACTER
    ),
    ;


    val textAttributesKey: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ygg.$name", default)
    val attributesDescriptor: AttributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
    val testSeverity: HighlightSeverity = HighlightSeverity(name, HighlightSeverity.INFORMATION.myVal)
}
