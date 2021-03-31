package org.wolkenproject.papaya.compiler;

public enum TokenType {
    None,
    IntegerNumber,
    BinaryString,
    Base16String,
    AsciiChar,
    DecimalNumber,
    AsciiString,
    ByteString,
    GenericAddress,
    ContractAddress,
    Identifier,
    ModifierKeyword,

    LogicalNotSymbol,
    AssignmentSymbol,

    AddSymbol,
    SubSymbol,
    MulSymbol,
    DivSymbol,
    ModSymbol,
    PowSymbol,

    XorSymbol,
    AndSymbol,
    OrSymbol,

    LogicalNotEqualsSymbol,
    EqualsSymbol,

    AddEqualsSymbol,
    SubEqualsSymbol,
    MulEqualsSymbol,
    DivEqualsSymbol,
    ModEqualsSymbol,
    PowEqualsSymbol,

    XorEqualsSymbol,
    AndEqualsSymbol,
    OrEqualsSymbol,

    NotSymbol,

    LogicalAndSymbol,
    LogicalOrSymbol,

    LogicalAndEqualsSymbol,
    LogicalOrEqualsSymbol,

    UnsignedRightShiftSymbol,
    RightShiftSymbol,
    LeftShiftSymbol,

    MemberAccessSymbol,
    StaticMemberAccessSymbol,
    LambdaSymbol,
    DoubleDotSymbol,
    CommaSymbol,
    HashTagSymbol,
    SemiColonSymbol,
    LessThanSymbol,
    GreaterThanSymbol,
    LessThanEqualsSymbol,
    GreaterThanEqualsSymbol,
    LeftParenthesisSymbol,
    RightParenthesisSymbol,
    LeftBracketSymbol,
    RightBracketSymbol,
    LeftBraceSymbol,
    RightBraceSymbol,
    ForKeyword,
    WhileKeyword,
    BreakKeyword,
    ContinueKeyword,
    PassKeyword,
    ReturnKeyword,
    FunctionKeyword,
    ContractKeyword,
    ModuleKeyword,
    ClassKeyword,
    StructKeyword,
    ExtendsKeyword,
    ImplementsKeyword,

    IncrementSymbol,
    DecrementSymbol,
    ColonEqualsSymbol,




    Parenthesis,
    Braces,
    Brackets,

    FieldDeclaration,
    AssignmentStatement,
}
