package com.techacademy.constants;

// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // 半角英数字チェックエラー
    HALFSIZE_ERROR,
    // 桁数(8桁~16桁以外)チェックエラー
    RANGECHECK_ERROR,
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR,
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR,
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR,
    // 日付チェックエラー
    DATECHECK_ERROR,
    // コメントチェックエラー
    COMMENTCHECK_ERROR,
    // ファイル名文字数(100文字以下)チェックエラー
    FILENAME_RANGECHECK_ERROR,
    // ファイルサイズチェックエラー
    FILESIZECHECK_ERROR,
    // 画像ファイルチェックエラー
    IMAGEFILECHECK_ERROR,
    // PDFのページ数チェックエラー
    PDFPAGECHECK_ERROR,
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS;

}
