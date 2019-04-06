import unittest
from big_num import *


class BigNumTest(unittest.TestCase):
    def test_division(self):
        self.assertEqual(BigNum.one() // BigNum.one(), BigNum.one(), "1 // 1 == 1")
        self.assertEqual(BigNum.zero() // BigNum.one(), BigNum.zero(), "0 // 1 == 0")
        self.assertEqual(BigNum.h("42") // BigNum.h("03"), BigNum.h("16"))
        self.assertEqual(BigNum.h("42") // BigNum.h("16"), BigNum.h("03"))
        self.assertEqual(BigNum.h("43") // BigNum.h("03"), BigNum.h("16"))

        self.assertEqual(BigNum.h("06260060") // BigNum.h("1234"), BigNum.h("5678"))
        self.assertEqual(BigNum.h("06263F29") // BigNum.h("5678"), BigNum.h("1234"))
        self.assertEqual(BigNum.h("06260FE3C9") // BigNum.h("56789A"), BigNum.h("1234"))
        self.assertEqual(
            BigNum.h("FFFFFE000001") // BigNum.h("FFFFFF"), BigNum.h("FFFFFF")
        )
        self.assertEqual(
            BigNum.h("FFFFFE0CFEDC") // BigNum.h("FFFFFF"), BigNum.h("FFFFFF")
        )
        self.assertEqual(
            BigNum.h("FAFD0318282820") // BigNum.h("FEFDFC"), BigNum.h("FBFAF9F8")
        )
        self.assertEqual(
            BigNum.h("FAFD0318C3D9EF") // BigNum.h("FEFDFC"), BigNum.h("FBFAF9F8")
        )

        self.assertEqual(BigNum.h("100000000") // BigNum.h("20000"), BigNum.h("8000"))

    def test_fast_division(self):
        old_divmod = BigNum.__divmod__
        try:
            BigNum.__divmod__ = BigNum.fast_divmod
            self.test_division()
        finally:
            BigNum.__divmod__ = old_divmod


if __name__ == "__main__":
    unittest.main()
