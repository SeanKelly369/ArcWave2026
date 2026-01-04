import UIKit
import SwiftUI
import ComposeApp

final class ComposeVCStore: ObservableObject {
    let vc: UIViewController = MainViewControllerKt.MainViewController()
}

struct ComposeView: UIViewControllerRepresentable {
    let vc: UIViewController

    func makeUIViewController(context: Context) -> UIViewController { vc }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @StateObject private var store = ComposeVCStore()

    var body: some View {
        ComposeView(vc: store.vc)
            .ignoresSafeArea()
    }
}

