import React from 'react';
import { AlertTriangle } from 'lucide-react';
import { Modal } from './Modal';

interface ConfirmDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  isDanger?: boolean;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  isDanger = true
}) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title} maxWidth="sm">
      <div className="space-y-4">
        <div className="flex items-start gap-3">
          {isDanger && (
            <div className="p-3 bg-red-500/10 text-red-400 rounded-xl">
              <AlertTriangle size={24} />
            </div>
          )}
          <p className="text-gray-300 text-sm leading-relaxed">{message}</p>
        </div>
        <div className="flex items-center justify-end gap-3 pt-4 border-t border-[#2C2C2C]">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium rounded-xl text-gray-300 bg-[#252525] hover:bg-[#2F2F2F] transition"
          >
            {cancelText}
          </button>
          <button
            type="button"
            onClick={() => {
              onConfirm();
              onClose();
            }}
            className={`px-4 py-2 text-sm font-bold rounded-xl transition ${
              isDanger
                ? 'bg-red-600 hover:bg-red-700 text-white shadow-lg shadow-red-600/20'
                : 'bg-amber-500 hover:bg-amber-600 text-black'
            }`}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </Modal>
  );
};
