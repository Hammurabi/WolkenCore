package org.wolkenproject.core.papaya.compiler;

public enum TokenType {
    None,
    IntegerNumber,
    BinaryString,
    Base16String,
    DecimalNumber,
    AsciiString,
    ByteString,
    GenericAddress,
    ContractAddress,
    Identifier,

    LogicalNotSymbol,
    AssignmentSymbol,

    AddSymbol,
    SubSymbol,
    MulSymbol,
    DivSymbol,
    ModSymbol,

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
}
